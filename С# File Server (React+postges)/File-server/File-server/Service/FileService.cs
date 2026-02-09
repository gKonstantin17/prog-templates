using File_server.Entity;
using File_server.Repository;

namespace File_server.Service
{
    public class FileService
    {
        private FileRepository repository;
        private UserService userService;
        private const string UPLOAD_PATH = "uploads";
        private readonly IWebHostEnvironment _environment;

        public FileService(FileRepository repository, UserService userService, IWebHostEnvironment environment)
        {
            this.repository = repository;
            this.userService = userService;
            _environment = environment;
        }

        // загрузить файл
        public async Task<FileData> saveFile(IFormFile file)
        {
            string directory = "files";
            return await save(file,directory);
        }
        // загрузить фото
        public async Task<FileData> savePhoto(long userId, IFormFile photo)
        {
            // Найти пользователя
            var user = await userService.findById(userId);
            if (user.Photo != null)
            {
                FileData oldPhoto = repository.getByPath(user.Photo).Result;
                await delete(oldPhoto.Id); // удалить
            }

            string directory = "photos";
            var result = await save(photo, directory);

            await userService.changePhoto(user, result);
            return result;
        }

        // сохранить файл на сервер и в бд
        private async Task<FileData> save(IFormFile file, string directory)
        {
            if (file == null || file.Length == 0)
                return null; // файл не выбран

            // название файла и путь
            string fileName = Path.GetFileNameWithoutExtension(file.FileName)
                           + "_"
                           + DateTime.Now.ToString("yyyyMMddHHmmss")
                           + Path.GetExtension(file.FileName);

            // Формируем полный путь на диске
            var wwwrootPath = _environment.WebRootPath;
            var uploadPath = Path.Combine(wwwrootPath, "uploads", directory);
            var fullPath = Path.Combine(uploadPath, fileName);

            // Путь для хранения в БД (относительный)
            var dbPath = $"uploads/{directory}/{fileName}";

            // сохранение файла на сервер
            using (var stream = new FileStream(fullPath, FileMode.Create))
            {
                await file.CopyToAsync(stream);
            }

            // сохранение в бд
            return await repository.save(new FileDto
            {
                Name = file.FileName,
                RealName = fileName,
                Path = dbPath,
                UploadDate = DateTime.UtcNow,
                Type = file.ContentType ?? "application/octet-stream", // Значение по умолчанию
                Size = file.Length,
                IsDeleted = false
            });
        }

        // получить список файлов
        public async Task<List<FileData>> get() => await repository.get();

        // найти и скачать файл
        public async Task<FileDownloadResult> downloadFile(long id)
        {
            FileData fileData = await repository.getById(id);

            var fullPath = Path.Combine(_environment.WebRootPath ?? _environment.ContentRootPath, fileData.Path);

            if (!File.Exists(fullPath))
                return null;


            var fileBytes = await File.ReadAllBytesAsync(fileData.Path);

            return new FileDownloadResult
            {
                FileBytes = fileBytes,
                FileName = fileData.Name ?? "file", // Значение по умолчанию
                ContentType = fileData.Type ?? "application/octet-stream" // Значение по умолчанию
            };
        }

        public class FileDownloadResult
        {
            public byte[] FileBytes { get; set; }
            public string FileName { get; set; }
            public string ContentType { get; set; }
        }

        // Мягкое удаление файла 
        public async Task<bool> SoftDeleteFile(long fileId)
        {
            var result = await repository.softDelete(fileId);
            return result != null;
        }

        // удалить файл с сервера и бд
        public async Task<bool> delete(long fileId)
        {
            var fileData = await repository.getById(fileId);

            // Удаляем файл с диска
            var physicalPath = fileData.Path;
            if (System.IO.File.Exists(physicalPath))
                System.IO.File.Delete(physicalPath);

            // Удаляем запись из БД
            await repository.delete(fileId);

            return true;
        }
        // удалить фото
        public async Task<bool> deletePhoto(User user)
        { 
            if (user.Photo == null)
                return false;

            FileData oldPhoto = repository.getByPath(user.Photo).Result;

            await userService.changePhoto(user, new FileData()); // заменить на null
            return await delete(oldPhoto.Id); // удалить с сервера
        }

    }
    // для фото можно сделать проверку, что файл явяляется изображением
    // проверку на вес файла и его расширение
}

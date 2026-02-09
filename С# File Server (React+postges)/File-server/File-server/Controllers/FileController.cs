using File_server.Service;
using File_server.Entity;
using Microsoft.AspNetCore.Mvc;

namespace File_server.Controllers
{
    [Route("api/file")]
    [ApiController]
    public class FileController : ControllerBase
    {
        private FileService service;
        public FileController(FileService service)
        {
            this.service = service;
        }
        // посмотреть файлы
        [HttpGet("/files")]
        public async Task<IActionResult> get()
        {
            var result = await service.get();
            return Ok(result);
        }
        // скачать файл
        [HttpGet("/download/{id}")]
        public async Task<IActionResult> downloadFile(long id)
        {
            var result = await service.downloadFile(id);
            if (result == null) return BadRequest("Файл не найден");
            return File(result.FileBytes, result.ContentType, result.FileName);
        }
        // загрузить файл
        [HttpPost("/upload-file")]
        public async Task<IActionResult> uploadFile(IFormFile file)
        {
            var result = await service.saveFile(file);
            if (result == null)
                return BadRequest("файл не выбран");
            return Ok(result);
        }
        // загрузить фото
        [HttpPost("/upload-photo")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> uploadPhoto([FromForm] long userId, [FromForm] IFormFile file)
        {
            var result = await service.savePhoto(userId, file);
            if (result == null)
                return BadRequest("Ошибка загрузки фото");
            return Ok(result);
        }

        // удалить фото
        [HttpPost("/delete-photo")]
        public async Task<IActionResult> deletePhoto(User user)
        {
            var result = await service.deletePhoto(user);
            return Ok(result);
        }
       

        // удалить файл
        [HttpDelete("/delete/{id}")]
        public async Task<IActionResult> delete(long id)
        {
            var success = await service.delete(id);
            if (!success)
                return NotFound("Файл не найден");

            return Ok(new { Message = "Файл полностью удален" });
        }
    }
}

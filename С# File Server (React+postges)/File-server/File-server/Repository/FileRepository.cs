using File_server.Entity;
using Microsoft.EntityFrameworkCore;

namespace File_server.Repository;

public class FileRepository
{
    private readonly ApplicationContext _db;
    public FileRepository(ApplicationContext db)
    {
        _db = db;
    }

    // сохранить
    public async Task<FileData> save(FileDto file)
    {
        FileData data = new FileData
        {
            Name = file.Name,
            RealName = file.RealName,
            Path = file.Path,
            UploadDate = file.UploadDate,
            Type = file.Type,
            Size = file.Size,
            IsDeleted = file.IsDeleted
        };
        var result = await _db.FileData.AddAsync(data);
        await _db.SaveChangesAsync();
        return result.Entity;
    }

    // список файлов кроме "удаленных" и фото
    public async Task<List<FileData>> get()
    {
        return await _db.FileData
            .Where(f => f.IsDeleted == false &&
                       f.Path != null &&
                       !EF.Functions.Like(f.Path, "uploads/photos/%"))
            .ToListAsync();
    }

    // найти файл по id
    public async Task<FileData> getById(long id) => await _db.FileData.FindAsync(id);

    // найти файл по path
    public async Task<FileData> getByPath(string path)
    {
        return await _db.FileData
            .Where(f => f.Path == path)
            .FirstOrDefaultAsync();
    }

    // мягкое удаление
    public async Task<FileData> softDelete(long id)
    {
        var data = await _db.FileData.FindAsync(id);
        data.IsDeleted = true;
        await _db.SaveChangesAsync();
        return data;
    }

    // полное удаление
    public async Task<FileData> delete (long id)
    {
        var data = await _db.FileData.FindAsync(id);
        _db.Remove(data);
        await _db.SaveChangesAsync();
        return data;
    }

}

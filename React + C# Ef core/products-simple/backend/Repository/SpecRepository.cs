using kis.DTO;
using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis.Repository
{
    public class SpecRepository
    {
        // Репозиторий делает запросы в бд, по вызову из сервиса
        private readonly ApplicationContext _db;
        public SpecRepository(ApplicationContext db)
        {
            _db = db;
        }

        public async Task<List<Specification>?> get()
        {   // таблицу Specification в бд достать в виде списка
            return await _db.Specification.ToListAsync();
        }

        public async Task<Specification?> findById(long id)
        {
            try
            {  // из таблицы Specification в бд достать запись, в которой совпадает id с выбранным
                return await _db.Specification.SingleAsync(o => o.Id == id);
            }
            catch (InvalidOperationException)
            {
                // Если запись не найдена, возвращаем null
                return null;
            }
        }

        public async Task<List<Specification>?> getComponentsById(long id)
        {  // из таблицы Specification в бд достать список, в которой совпадает Parent_id с выбранным
            return await _db.Specification
                .Where(s => s.Parent_id == id)
                .ToListAsync();
        }
        public async Task<List<Specification>?> getRoots()
        { //  из таблицы Specification в бд достать список с Level = 0
            return await _db.Specification
                .Where(s => s.Level == 0)
                .ToListAsync();
        }
        public async Task<long> create(SpecDto spec)
        {
            // создать запись в бд Specification, с полями из dto
            var result = await _db.Specification.AddAsync(new Specification
            {
                Level = (short)spec.Level,
                Name = (string)spec.Name,
                Count = (int)spec.Count,
                Parent_id = (long?)spec.Parent_id
            });
            await _db.SaveChangesAsync(); // сохранить
            return result.Entity.Id; // показать id
        }

        public async Task<Specification?> update(long id, SpecDto dto)
        {
            // есть ли товар в бд?
            var spec = await findById(id);

            if (spec != null) // если есть, то изменить указанные поля
            { // если не указали поле, то оно равно null -> измения не применятся

                if (dto.Level != null) spec.Level = (short)dto.Level;
                if (dto.Name != null) spec.Name = dto.Name;
                if (dto.Count != null) spec.Count = (int)dto.Count;
                if (dto.Parent_id != null) spec.Parent_id = dto.Parent_id;
                var result = _db.Specification.Update(spec); // изменить
                await _db.SaveChangesAsync(); // сохранить
                return spec; // показать результат
            }
            else return null;
        }

        public async Task<long?> delete(long id)
        {
            var spec = await findById(id); // найти товар
            if (spec == null) return null;  
            _db.Remove(spec); // если есть, то удалить
            await _db.SaveChangesAsync(); // сохранить
            return spec.Id; // показать что удалили
        }

    }
}

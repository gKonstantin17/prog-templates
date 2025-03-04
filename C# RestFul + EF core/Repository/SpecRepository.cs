using kis.DTO;
using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis.Repository
{
    public class SpecRepository
    {
        private readonly ApplicationContext _db;
        public SpecRepository(ApplicationContext db)
        {
            _db = db;
        }

        public async Task<List<Specification>?> get()
        {
            return await _db.Specification.ToListAsync();
        }

        public async Task<Specification?> findById(long id)
        {
            return await _db.Specification.SingleOrDefaultAsync(o => o.Id == id); ;
        }

        public async Task<List<Specification>?> getComponentsById(long id)
        {
            return await _db.Specification
                .Where(s => s.Parent_id == id)
                .ToListAsync();
        }


        public async Task<long> create(SpecCreateDto spec)
        {

            var result = await _db.Specification.AddAsync(new Specification
            {
                Level = spec.Level,
                Name = spec.Name,
                Count = spec.Count,
                Parent_id = spec.Parent_id
            });
            await _db.SaveChangesAsync();
            return result.Entity.Id;
        }

        public async Task<Specification?> update(long id, SpecUpdateDto dto)
        {
        
            var spec = await findById(id);

            if (spec != null)
            {
                if (dto.Level != null) spec.Level = (short)dto.Level;
                if (dto.Name != null) spec.Name = dto.Name;
                if (dto.Count != null) spec.Count = (int)dto.Count;
                if (dto.Parent_id != null) spec.Parent_id = dto.Parent_id;
                var result = _db.Specification.Update(spec);
                await _db.SaveChangesAsync();
                return spec;
            }
            else return null;
        }

        public async Task<long?> delete(long id)
        {
            var spec = await findById(id);
            if (spec != null) _db.Remove(spec);
            await _db.SaveChangesAsync();
            return spec.Id;
        }

    }
}

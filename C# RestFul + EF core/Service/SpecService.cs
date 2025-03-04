using kis.DTO;
using kis.Entity;
using kis.Repository;

namespace kis.Service
{
    public class SpecService
    {
        private SpecRepository repository;
        public SpecService(SpecRepository repository)
        {
            this.repository = repository;
        }

        public async Task<List<Specification>?> get() => await repository.get();

        public async Task<Specification?> findById(long id) => await repository.findById(id);

        public async Task<List<Specification>?> getComponentsById(long id) => await repository.getComponentsById(id);

        public async Task<List<Specification>?> findPartOf(long id)
        {
            var allSpecifications = get();
            var result = new List<Specification>();
            long? currentId = id;

            while (currentId.HasValue)
            {
                var specification = allSpecifications.Result.Find(s => s.Id == currentId);
                if (specification == null)
                {
                    break; // Если запись не найдена, прерываем цикл
                }

                result.Add(specification); 
                currentId = specification.Parent_id; // Переходим к родительской записи
            }

            return result;
        }

        public async Task<long?> create(SpecCreateDto spec)
        {
            Specification? parent = null;
            if (spec.Parent_id != null)
            {
                parent = await findById((long)spec.Parent_id);
                if (spec.Level - parent.Level != 1)
                    return -1;
            }
            if (spec.Count <= 0)
                return -2;
            return await repository.create(spec);
        }

        public async Task<Specification?> update(long id, SpecUpdateDto dto) => await repository.update(id, dto);

        public async Task<long?> delete(long id) => await repository.delete(id);


    }
}

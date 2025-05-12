using kis.DTO;
using kis.Entity;
using kis.Repository;
using Microsoft.AspNetCore.Components.Forms;

namespace kis.Service
{
    public class SpecService
    {
        // Сервис получает запрос из контроллера, обрабатывает его
        // использует репозиторий для запросов в бд
        private SpecRepository repository;
        public SpecService(SpecRepository repository)
        {
            this.repository = repository;
        }

        public async Task<List<Specification>?> get() => await repository.get();

        public async Task<Specification?> findById(long id) => await repository.findById(id);
        public async Task<List<SpecDtoWithChildren>> getAllHierarchy()
        {
            var listRoots = await repository.getRoots(); // корни - те, у кого parent - null
            var result = new List<SpecDtoWithChildren>();
            // для каждого корня нахождение подкомпонентов и добавление в список
            foreach (var root in listRoots) 
            {
                var rootWithChilden = await getComponentsById(root.Id);
                if (rootWithChilden != null)
                    result.Add(rootWithChilden);
            }
            return result;
        }

        private async Task<SpecDtoWithChildren?> getComponentsById(long id)
        {
            // нахождение записи в бд
            var spec = await repository.findById(id);
            if (spec == null) return null;
            // обертка в объект, в котором есть список children
            var rootDto = specToDtoWithChildren(spec);

            // Рекурсивно строим иерархию
            await BuildHierarchy(rootDto);

            return rootDto;
        }
        private async Task BuildHierarchy(SpecDtoWithChildren parentDto)
        {
            // список дочерних компонентов для текущего родителя
            var children = await repository.getComponentsById(parentDto.Id);

            if (children != null && children.Any())
            {
                foreach (var child in children)
                {
                    // Обернуть дочерний элемент
                    var childDto = specToDtoWithChildren(child);
                    // Продолжаем строим иерархию для дочернего элемента
                    await BuildHierarchy(childDto);
                    // Добавляем дочерний элемент в список Specifications родителя
                    parentDto.Specifications.Add(childDto);
                }
            }
        }

        private SpecDtoWithChildren specToDtoWithChildren(Specification spec)
        {
            return new SpecDtoWithChildren
            {
                Id = spec.Id,
                Level = spec.Level,
                Name = spec.Name,
                Count = spec.Count,
                Specifications = new List<SpecDtoWithChildren>()
            };
        }

        
        public async Task<SpecDtoWithChildren?> countComponentsById(long id)
        {
            // есть ли товар с таким id в бд?
            var spec = await repository.findById(id);
            if (spec == null) return null;

            // собираем товар и его дочерние компоненты в 1 объект
            var rootDto = specToDtoWithChildren(spec);
            await BuildHierarchy(rootDto);

            // Пересчитываем Count для листьев и удаляем промежуточные узлы
            ProcessHierarchy(rootDto);

            return rootDto;
        }

        private void ProcessHierarchy(SpecDtoWithChildren node)
        {
            // если у узла есть дочерние компоненты
            if (node.Specifications.Any())
            {
                // Создаем список
                var leaves = new List<SpecDtoWithChildren>();

                // для каждого дочернего элемента
                foreach (var child in node.Specifications)
                {
                    
                    ProcessHierarchy(child);
                    // Если у ребенка есть свои дети
                    if (child.Specifications.Any())
                    {
                        // умножаем их Count на Count текущего узла 
                        // добавляем их в список
                        foreach (var leaf in child.Specifications)
                        {
                            leaf.Count *= node.Count;
                            leaves.Add(leaf);
                        }
                    }
                    else // нет детей - умножаем его Count на Count родителя
                    {
                        child.Count *= node.Count;
                        leaves.Add(child);
                    }
                }

                // Заменяем дочерние элементы на найденные листья
                node.Specifications.Clear();
                node.Specifications.AddRange(leaves);
            }
        }


        public async Task<long?> create(SpecDto spec)
        {
            Specification? parent = null;
            // spec является корнем, то сразу создаем

            // если spec дочерний, то проверка данных
            if (spec.Parent_id != null)
            {
                // есть ли родитель?
                parent = await findById((long)spec.Parent_id);
                if (parent == null) return null; // родителя нет
                if (spec.Level - parent.Level != 1)
                    return null; // Уровень должен быть больше на 1
            }
            if (spec.Count <= 0)
                return null; // Количество должно быть больше 0

            // после проверок создаем
            var result = await repository.create(spec);
            return result;
        }

        public async Task<Specification?> update(long id, SpecDto dto) => await repository.update(id, dto);

        public async Task<List<long>?> delete(long id)
        {
            // есть ли товар в бд?
            var spec = await findById(id);
            if (spec == null) return null;

            // достаем все товары из бд
            var allSpecs = await get();
            // список, кого удалили (в ходе каскадного удаления)
            List<long> deletedList = new List<long> { id };

            // суем в очередь удаления
            var queue = new Queue<long>();
            queue.Enqueue(id);

            // удаляем текущий
            await repository.delete(id);

            while (queue.Count > 0)
            {
                // достаем из очереди
                var currentId = queue.Dequeue();
                // достаем список дочерних компонентов
                var children = allSpecs.Where(s => s.Parent_id == currentId).ToList();
                // каждый из дочерних элементов, добавляется в список и очередь, удаляется из бд
                foreach (var child in children)
                {
                    deletedList.Add(child.Id);
                    queue.Enqueue(child.Id);
                    await repository.delete(child.Id);
                }
            }
            return deletedList;
        }

    }
}

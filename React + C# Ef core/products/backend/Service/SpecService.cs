using kis.DTO.Spec;
using kis.Entity;
using kis.Repository;
using Microsoft.AspNetCore.Components.Forms;
using System.Collections.Generic;

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

        public async Task<SpecDtoWithChildren?> getComponentsById(long id)
        {
            var spec = await repository.findById(id);
            if (spec == null) return null;

            var rootDto = specToDtoWithChildren(spec);

            // Рекурсивно строим иерархию
            await BuildHierarchy(rootDto);

            return rootDto;
        }
        private async Task BuildHierarchy(SpecDtoWithChildren parentDto)
        {
            // Получаем дочерние элементы для текущего родителя
            var children = await repository.getComponentsById(parentDto.Id);

            if (children != null && children.Any())
            {
                foreach (var child in children)
                {
                    // Преобразуем дочерний элемент в DTO
                    var childDto = specToDtoWithChildren(child);

                    // Рекурсивно строим иерархию для дочернего элемента
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

        public async Task<List<SpecDtoWithChildren>>  getAllHierarchy()
        {
            var listRoots = await repository.getRoots();
            List<SpecDtoWithChildren> result = new List<SpecDtoWithChildren>();
            foreach (var root in listRoots)
            {
                var rootWithChilden = await getComponentsById(root.Id);
                if (rootWithChilden != null)
                    result.Add(rootWithChilden);
            }
            return result;
        }
        public async Task<SpecDtoWithChildren?> countComponentsById(long id)
        {
            var spec = await repository.findById(id);
            if (spec == null) return null;

            var rootDto = specToDtoWithChildren(spec);
            await BuildHierarchy(rootDto);

            // Пересчитываем Count для листьев и удаляем промежуточные узлы
            ProcessHierarchy(rootDto);

            return rootDto;
        }

        private void ProcessHierarchy(SpecDtoWithChildren node)
        {
            if (node.Specifications.Any())
            {
                // Создаем временный список для листьев
                var leaves = new List<SpecDtoWithChildren>();

                foreach (var child in node.Specifications)
                {
                    ProcessHierarchy(child);

                    if (child.Specifications.Any())
                    {
                        // Если у ребенка есть свои дети (листья), добавляем их в наш список
                        // и умножаем их Count на Count текущего узла (node)
                        foreach (var leaf in child.Specifications)
                        {
                            leaf.Count *= node.Count;
                            leaves.Add(leaf);
                        }
                    }
                    else
                    {
                        // Если это лист, просто умножаем его Count на Count родителя
                        child.Count *= node.Count;
                        leaves.Add(child);
                    }
                }

                // Заменяем дочерние элементы на найденные листья
                node.Specifications.Clear();
                node.Specifications.AddRange(leaves);
            }
        }
       

        public async Task<(long?,string)> create(SpecDto spec)
        {
            Specification? parent = null;
            if (spec.Parent_id != null)
            {
                parent = await findById((long)spec.Parent_id);
                if (parent == null) return (null, "Указанного родительского компонента не существует");
                if (spec.Level - parent.Level != 1)
                    return (null, "Уровень должен быть больше на 1"); // Уровень должен быть больше на 1
            }
            if (spec.Count <= 0)
                return (null, "Количество должно быть больше 0"); // Количество должно быть больше 0
            var result = await repository.create(spec);
            return (result, "Ok");
        }

        public async Task<Specification?> update(long id, SpecDto dto) => await repository.update(id, dto);

        public async Task<List<long>?> delete(long id)
        {
            var spec = await findById(id);
            if (spec == null) return null;
            
            var allSpecs = await get();
            List<long> deletedList = new List<long> { id };

            var queue = new Queue<long>();
            queue.Enqueue(id);

            await repository.delete(id);

            while (queue.Count > 0)
            {
                var currentId = queue.Dequeue();
                var children = allSpecs.Where(s => s.Parent_id == currentId).ToList();
                foreach (var child in children)
                {
                    deletedList.Add(child.Id); // Добавляем ID дочернего элемента в список
                    queue.Enqueue(child.Id); // Добавляем ID дочернего элемента в очередь для дальнейшего обхода
                    await repository.delete(child.Id);
                }
            }     
            return deletedList;
        }

    }
}

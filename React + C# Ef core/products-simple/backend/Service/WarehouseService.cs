using kis.DTO;
using kis.Entity;
using kis.Repository;
using Microsoft.AspNetCore.Mvc;

namespace kis.Service
{
    public class WarehouseService
    {
        // Сервис получает запрос из контроллера, обрабатывает его
        // использует репозиторий для запросов в бд
        // здесь задейстовованы сервисы из других сущности
        private WarehouseRepository repository;
        private SpecService specService;
        private OrderRepository orderRepository;
        public WarehouseService(WarehouseRepository repository, SpecService specService, OrderRepository orderRepository)
        {
            this.repository = repository;
            this.specService = specService;
            this.orderRepository = orderRepository;
        }

        public async Task<List<WarehouseDto>?> findByDate(DateTime date)
        {
            // список с записями таблицы до указанной даты
            List<WarehouseDto> result = await repository.findByDate(date);
            if (result.Count == 0) return null;// В это время склад - пустой

            // чтобы выбрать актуальные записи из склада на указанную дату
            // Группируем по Product_Id и для каждой группы выбираем запись с максимальным Id и UpdateDate
            var groupedProducts = result
                .GroupBy(p => p.Product_Id)
                .Select(g => g.OrderByDescending(p => p.Id)
                              .ThenByDescending(p => p.UpdateDate)
                              .First())
                .ToList();

            // Создаем словарь для суммирования Count
            Dictionary<long, int> idToCount = new Dictionary<long, int>();

            // перебираем записи и суммируем количество
            foreach (var product in result)
            {
                if (!idToCount.ContainsKey(product.Product_Id))
                    idToCount.Add(product.Product_Id, product.Count);
                else
                    idToCount[product.Product_Id] += product.Count;
            }

            // Создаем итоговый список, используя данные из groupedProducts и суммированные Count из idToCount
            List<WarehouseDto> resultList = groupedProducts
                .Select(p => new WarehouseDto
                {
                    Product_Id = p.Product_Id,
                    Count = idToCount[p.Product_Id],
                    Id = p.Id,
                    UpdateDate = p.UpdateDate
                })
                .ToList();

            return resultList; // всё ок
        }

        public async Task<List<WarehouseDto>> getHistoryByProductId(long id) => 
            await repository.getHistoryByProductId(id); //сразу вызывает репозиторий, который достает историю изменений

        public async Task<List<WhDeficitFullDto>?> checkProducts()
        {
            // достать список заказов
            var listOrders = await orderRepository.get();
            if (!listOrders.Any()) // если список пуст
                return null;// нет заказов

            // словарь (product_id, count) из заказов
            var specsFromOrders = new Dictionary<long, int>();
            foreach (var order in listOrders)
            {
                // для каждого заказа посчитать количество нужных компонентов
                var specWithChildren = await specService.countComponentsById((long)order.Product_id);
                if (!specWithChildren.Specifications.Any()) // если товар в заказе без дочерних компонентов
                {
                    var children = specWithChildren; // товар принимаем за дочерний элемент

                    children.Count = order.Count;  // пересчитываем количество исходя из заказа
                    if (specsFromOrders.ContainsKey(children.Id)) // дочерний элемент суем в словарь
                        specsFromOrders[children.Id] += (int)children.Count;
                    else
                        specsFromOrders.Add(children.Id, (int)children.Count);
                }
                else // у товара в заказе есть дочерние компоненты
                    foreach (var children in specWithChildren.Specifications)
                    {  // тоже самое, но без  var children = specWithChildren; 
                        children.Count *= order.Count;
                        if (specsFromOrders.ContainsKey(children.Id))
                            specsFromOrders[children.Id] += (int)children.Count;
                        else
                            specsFromOrders.Add(children.Id, (int)children.Count);
                    }
            }

            // словарь (product_id, count) из склада
            var specsFromWh = new Dictionary<long, int>();

            // список товаров в складе на данный момент
            var productsInWh = await findByDate(DateTime.Now);
            foreach (var product in productsInWh)
            {
                // аналогично товарам из заказа пересчитываем количество
                var componentsOfProduct = await specService.countComponentsById(product.Product_Id);
                if (!componentsOfProduct.Specifications.Any())
                {
                    var children = componentsOfProduct;
                    children.Count = product.Count;
                    if (specsFromWh.ContainsKey(children.Id))
                        specsFromWh[children.Id] += (int)children.Count;
                    else
                        specsFromWh.Add(children.Id, (int)children.Count);
                }
                foreach (var children in componentsOfProduct.Specifications)
                {
                    children.Count *= product.Count;
                    if (specsFromWh.ContainsKey(children.Id))
                        specsFromWh[children.Id] += (int)children.Count;
                    else
                        specsFromWh.Add(children.Id, (int)children.Count);
                }
            }


            // список с результатом подсчета дифицита
            var resultList = new List<WhDeficitFullDto>();

            // сравнение словарей заказов и склада
            // необходимое коль-во (requiredCount) берется сразу из словаря заказа
            // имеющиеся коль-во (availableCount) берется из словаря склада
            // сколько осталось (missingCount) вычисляется 
            foreach (var (specFromOrder, requiredCount) in specsFromOrders)
            {
                int availableCount = specsFromWh.ContainsKey(specFromOrder) ? specsFromWh[specFromOrder] : 0; ;
                int missingCount = (requiredCount - availableCount);
                if (missingCount < 0) missingCount = 0;

                /* Результат засунуть в список в виде
                {
                    "productId": 2,
                        "whDeficit": {
                            "required": 66,
                            "available": 18,
                            "missing": 48
                        }
                }
                */
                resultList.Add(new WhDeficitFullDto
                {
                    ProductId = specFromOrder,
                    WhDeficit = new WarehouseDeficitDto
                    {
                        Required = requiredCount,
                        Available = availableCount,
                        Missing = missingCount
                    }
                });

            }

            return resultList;
        }
        public async Task<WarehouseDto?> change(WarehouseChangeDto dto)
        {
            if (dto.Number <= 0) // если количество отрицательное
            { // то находим товар на данный момент (latest version) с вычисленным  количеством
                var latestWarehouse = await repository.findByProduct(dto.ProductId);
                if (latestWarehouse != null)
                {
                    // если товара хватает
                    if ((latestWarehouse.Value.Item1.Count + dto.Number) >= 0)
                    {
                        // создаем запись в истории
                        var newCreated = await repository.create(dto.ProductId, dto.Number);
                        // выводим результат с итоговым количеством
                        var (warehouse, fk) = newCreated.Value;
                        var result = new WarehouseDto
                        {
                            Id = warehouse.Id,
                            Count = warehouse.Count,
                            Product_Id = fk,
                            UpdateDate = warehouse.UpdateDate
                        };
                        return result;// Ok
                    }
                    return null; // Товара не хватает
                }
                return null; // Товар не найден на складе
            }

            // если количество положительное, создаем запись в истории
            var created = await repository.create(dto.ProductId, dto.Number);

            if (created == null)
                return null; // Товар не найден в спецификации
            else
            {
                // выводим результат с итоговым количеством
                var (warehouse, fk) = created.Value;
                var result = new WarehouseDto
                {
                    Id = warehouse.Id,
                    Count = warehouse.Count,
                    Product_Id = fk,
                    UpdateDate = warehouse.UpdateDate
                };
                return result;
            }

        }
       
        public async Task<long?> delete(long id) => await repository.delete(id); // сразу вызывает репозиторий, который удаляет из истории
    }
}
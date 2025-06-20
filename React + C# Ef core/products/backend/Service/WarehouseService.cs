using kis.DTO.Spec;
using kis.DTO.Wh;
using kis.Entity;
using kis.Repository;
using Microsoft.AspNetCore.Mvc;
using System.Collections;

namespace kis.Service
{
    public class WarehouseService
    {
        private WarehouseRepository repository;
        private SpecService specService;
        private OrderRepository orderRepository;
        public WarehouseService(WarehouseRepository repository, SpecService specService, OrderRepository orderRepository)
        {
            this.repository = repository;
            this.specService = specService;
            this.orderRepository = orderRepository;
        }

        public async Task<List<WarehouseDto>> getHistoryByProductId(long id) => 
            await repository.getHistoryByProductId(id);

        public async Task<(List<WhWithProduct>?, string)> findByDate(DateTime date)
        {
            List<WhWithProduct> result = await repository.findByDate(date);
            if (result.Count == 0) return (null, "На этот момент склад - пустой");

            // Группируем по Product_Id и для каждой группы выбираем запись с максимальным Id и UpdateDate
            var groupedProducts = result
                .GroupBy(p => p.Product_Id)
                .Select(g => g.OrderByDescending(p => p.Id)
                              .ThenByDescending(p => p.UpdateDate)
                              .First())
                .ToList();

            // Создаем словарь для суммирования Count
            Dictionary<long, int> idToCount = new Dictionary<long, int>();

            foreach (var product in result)
            {
                if (!idToCount.ContainsKey(product.Product_Id))
                    idToCount.Add(product.Product_Id, product.Count);
                else
                    idToCount[product.Product_Id] += product.Count;
            }

            // Создаем итоговый список, используя данные из groupedProducts и суммированные Count из idToCount
            List<WhWithProduct> resultList = groupedProducts
                .Select(p => new WhWithProduct
                {
                    Product_Id = p.Product_Id,
                    Count = idToCount[p.Product_Id],
                    Id = p.Id,
                    UpdateDate = p.UpdateDate,
                    Product = p.Product
                    // Добавьте другие необходимые свойства здесь
                })
                .ToList();

            return (resultList, "Ok");
        }
       

        public async Task<(WarehouseDto?, string)> change(WarehouseChangeDto dto)
        {
            if (dto.Number <= 0)
            {
                var latestWarehouse = await repository.findByProduct(dto.ProductId);
                if (latestWarehouse != null)
                {
                    if ((latestWarehouse.Value.Item1.Count + dto.Number) >= 0)
                    {
                        var newCreated = await repository.create(dto.ProductId, dto.Number);
                        var (warehouse, fk) = newCreated.Value;
                        var result = new WarehouseDto
                        {
                            Id = warehouse.Id,
                            Count = warehouse.Count,
                            Product_Id = fk,
                            UpdateDate = warehouse.UpdateDate
                        };
                        return (result, "Ok");
                    }
                    return (null, "Товара не хватает");
                }
                return (null, "Товар не найден на складе");
            }
                
            var created = await repository.create(dto.ProductId, dto.Number);
        
            if (created == null)
                return (null, "Товар не найден в спецификации");
            else
            {
                var (warehouse, fk) = created.Value;
                var result = new WarehouseDto
                {
                    Id = warehouse.Id,
                    Count = warehouse.Count,
                    Product_Id = fk,
                    UpdateDate = warehouse.UpdateDate
                };
                return (result, "Ok");
            }
                
        }
        public async Task<long?> delete(long id) => await repository.delete(id);

        
        public async Task<(List<ResultOfCheckWh>?, string)> checkProducts()
        {
            var uncompletedOrders = await orderRepository.getUncompletedOrders();
            if (!uncompletedOrders.Any()) // список пуст
                return (null, "нет незавершненных заказов");

            // словарь (product_id, count) из заказов с статусом false 
            var specsFromOrders = new Dictionary<long, ComponentInfo>();
            foreach (var order in uncompletedOrders)
            {
                var specWithChildren = await specService.countComponentsById((long)order.Product_id);
                if (!specWithChildren.Specifications.Any())
                {
                    var children = specWithChildren;
                    children.Count = order.Count;
                    if (specsFromOrders.ContainsKey(children.Id))
                        specsFromOrders[children.Id].Count += (int)children.Count;
                    else
                        specsFromOrders.Add(children.Id, new ComponentInfo
                        {
                            ProductName = children.Name,
                            Count = (int)children.Count
                        });
                }
                foreach (var children in specWithChildren.Specifications)
                {
                    children.Count *= order.Count;
                    if (specsFromOrders.ContainsKey(children.Id))
                        specsFromOrders[children.Id].Count += (int)children.Count;
                    else
                        specsFromOrders.Add(children.Id, new ComponentInfo
                        {
                            ProductName = children.Name,
                            Count = (int)children.Count
                        });
                }
            }

            // словарь (product_id, count) из склада
            var specsFromWh = new Dictionary<long, ComponentInfo>();

            var productsInWh = await findByDate(DateTime.Now);
            foreach (var product in productsInWh.Item1)
            {
                var componentsOfProduct = await specService.countComponentsById(product.Product_Id);
                if (!componentsOfProduct.Specifications.Any())
                {
                    var children = componentsOfProduct;
                    children.Count = product.Count;
                    if (specsFromWh.ContainsKey(children.Id))
                        specsFromWh[children.Id].Count += (int)children.Count;
                    else
                        specsFromWh.Add(children.Id, new ComponentInfo
                        {
                            ProductName = children.Name,
                            Count = (int)children.Count
                        });
                }
                foreach (var children in componentsOfProduct.Specifications)
                {
                    children.Count *= product.Count;
                    if (specsFromWh.ContainsKey(children.Id))
                        specsFromWh[children.Id].Count += (int)children.Count;
                    else
                        specsFromWh.Add(children.Id,new ComponentInfo
                        {
                            ProductName = children.Name,
                            Count = (int)children.Count
                        });
                }
            }

            var resultList = new List<ResultOfCheckWh>();

            foreach (var (specFromOrder, compInfo) in specsFromOrders)
            {
                int requiredCount = compInfo.Count;
                int availableCount = specsFromWh.ContainsKey(specFromOrder) ? specsFromWh[specFromOrder].Count : 0; ;
                int missingCount = (requiredCount - availableCount);
                if (missingCount < 0) missingCount = 0;

                resultList.Add(new ResultOfCheckWh
                {
                    Product = compInfo.ProductName,
                    ProductId = specFromOrder,
                    Result = new ResultCheck
                    {
                        Required = requiredCount,
                        Available = availableCount,
                        Missing = missingCount,
                        Status = missingCount == 0? "В наличии" : "Не достаточно"
                    }
                });
                    
            }
            return (resultList, "Ok");
        }
    }
}
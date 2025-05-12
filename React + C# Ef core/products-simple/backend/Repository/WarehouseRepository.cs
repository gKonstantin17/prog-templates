using kis.DTO;
using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis.Repository
{
    public class WarehouseRepository
    {
        // Репозиторий делает запросы в бд, по вызову из сервиса
        // здесь ещё задестовован репозиторий для товаров
        private readonly ApplicationContext _db;
        private SpecRepository specRepository;
        public WarehouseRepository(ApplicationContext db, SpecRepository specRepository)
        {
            _db = db;
            this.specRepository = specRepository;
        }

        public async Task<List<WarehouseDto>> getHistoryByProductId(long id)
        { // из таблицы Warehouse в бд достать записи, в которой совпадает Product_Id с выбранным и засунуть в список
            return await _db.Warehouse
                .Where(w => w.Product.Id == id)
                .Select(w => new WarehouseDto
                {
                    Id = w.Id,
                    Product_Id = w.Product.Id,
                    Count = w.Count,
                    UpdateDate = w.UpdateDate
                })
            .ToListAsync();
        }

        public async Task<List<WarehouseDto>?> findByDate(DateTime date)
        { // из таблицы Warehouse в бд достать записи до указанной даты включительно
            return await _db.Warehouse
                .Where(w => w.UpdateDate <= date)
                .Select(w => new WarehouseDto
                {
                    Id = w.Id,
                    Product_Id = w.Product.Id,
                    Count = w.Count,
                    UpdateDate = w.UpdateDate
                })
                .ToListAsync();
        }

        public async Task<(Warehouse, long)?> findByProduct(long id)
        { // найти товар и посчитать количество на данный момент
            try
            {  // из таблицы Warehouse в бд достать записи, в которой совпадает Product_Id с выбранным
                var warehouseList = await _db.Warehouse
                    .Where(w => w.Product.Id == id)
                    .Select(w => new WarehouseDto
                    {
                        Id = w.Id,
                        Product_Id = w.Product.Id,
                        Count = w.Count,
                        UpdateDate = w.UpdateDate
                    })
                    .ToListAsync();

                // просуммировать количество
                int totalCount = warehouseList.Sum(w => w.Count);
                // выбрать последнюю по дате
                var latestWarehouse = warehouseList
                            .OrderByDescending(w => w.UpdateDate)
                            .First();

                // оформить результать в виде объекта
                var warehouse = new Warehouse
                {
                    Id = latestWarehouse.Id,
                    Product = await specRepository.findById(latestWarehouse.Product_Id),
                    Count = totalCount,
                    UpdateDate = latestWarehouse.UpdateDate
                };
                return (warehouse, warehouse.Product.Id);

            }
            catch (InvalidOperationException)
            { // Ошибка возникает когда запись не найдена, возвращаем null
                return null;
            }

        }
        public async Task<(Warehouse, long)?> create(long productId, int number)
        {
            // найти товар
            var product = await specRepository.findById(productId);
            if (product == null) return null;


            var result = await _db.Warehouse.AddAsync(new Warehouse // добавить запись с изменением на склад
            {
                Count = number,
                Product = product,
                UpdateDate = DateTime.Now
            });
            await _db.SaveChangesAsync(); // сохранить
            return await findByProduct(product.Id); // вывести результат с итоговым количеством
        }

        public async Task<long?> delete(long id)
        {
            // найти запись, для этого из таблицы Warehouse в бд достать записи, в которой совпадает Id с выбранным
            var warehouse = await _db.Warehouse.SingleAsync(o => o.Id == id);
            if (warehouse != null) _db.Remove(warehouse); // удалить
            await _db.SaveChangesAsync(); // сохранить 
            return warehouse.Id; // показать что удалили
        }
    }
}

using kis.DTO.Wh;
using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis.Repository
{
    public class WarehouseRepository
    {
        private readonly ApplicationContext _db;
        private SpecRepository specRepository;
        public WarehouseRepository(ApplicationContext db, SpecRepository specRepository)
        {
            _db = db;
            this.specRepository = specRepository;
        }

        public async Task<List<WarehouseDto>> getHistoryByProductId(long id)
        {
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

        public async Task<List<WhWithProduct>?> findByDate(DateTime date)
        {
            return await _db.Warehouse
                .Where(w => w.UpdateDate <= date)
                .Select(w => new WhWithProduct
                {
                    Id = w.Id,
                    Product_Id = w.Product.Id,
                    Count = w.Count,
                    UpdateDate = w.UpdateDate,
                    Product = w.Product.Name
                })
                .ToListAsync();
        }

        public async Task<(Warehouse, long)?> findByProduct(long id)
        {
            try
            {
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


                int totalCount = warehouseList.Sum(w => w.Count);

                var latestWarehouse = warehouseList
                            .OrderByDescending(w => w.UpdateDate)
                            .First();

                var warehouse = new Warehouse
                {
                    Id = latestWarehouse.Id,
                    Product = await specRepository.findById(latestWarehouse.Product_Id),
                    Count = totalCount,
                    UpdateDate = latestWarehouse.UpdateDate
                };
                return (warehouse, warehouse.Product.Id);

            } catch (InvalidOperationException) {
                return null;
            }

        }
        public async Task<(Warehouse, long)?> create(long productId,int number)
        {
            var product = await specRepository.findById(productId);
            if (product == null) return null;

            var result = await _db.Warehouse.AddAsync(new Warehouse
            {
                Count = number,
                Product = product,
                UpdateDate = DateTime.Now
            });
            await _db.SaveChangesAsync();
            return await findByProduct(product.Id);
        }

        public async Task<long?> delete(long id)
        {
            var warehouse = await _db.Warehouse.SingleAsync(o => o.Id == id);
            if (warehouse != null) _db.Remove(warehouse);
            await _db.SaveChangesAsync();
            return warehouse.Id;
        }
    }
}

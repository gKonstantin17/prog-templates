using kis.DTO.Order;
using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis.Repository
{
    public class OrderRepository 
    {
        private readonly ApplicationContext _db;
        public OrderRepository(ApplicationContext db)
        {
            _db = db;
        }

        public async Task<List<OrderWithProduct>?> get()
        {
            return await _db.Order
                .Select(o => new OrderWithProduct
                {
                    Id = o.Id,
                    CreateDate = o.CreateDate,
                    Deadline = o.Deadline,
                    Count = o.Count,
                    Price = o.Price,
                    TotalPrice = o.TotalPrice,
                    Status = o.Status,
                    Product_id = o.Product.Id,
                    Product = o.Product.Name
                })
                .ToListAsync();
        }
        public async Task<Order?> findByIdWithFK(long id)
        {
            try
            {
                return await _db.Order
                   .Where(o => o.Id == id)
                   .SingleAsync();
            }
            catch (InvalidOperationException)
            {   // Ошибка возникает когда запись не найдена, возвращаем null
                return null;
            }
            
        }

        public async Task<OrderDto?> findById(long id)
        {
            try
            {
                return await _db.Order
               .Where(o => o.Id == id)
               .Select(o => new OrderDto
               {
                   Id = o.Id,
                   CreateDate = o.CreateDate,
                   Deadline = o.Deadline,
                   Count = o.Count,
                   Price = o.Price,
                   TotalPrice = o.TotalPrice,
                   Status = o.Status,
                   Product_id = o.Product.Id
               })
               .SingleAsync();
            }
            catch (InvalidOperationException)
            {   // Ошибка возникает когда запись не найдена, возвращаем null
                return null;
            }
            
        }

        public async Task<List<OrderDto>> getUncompletedOrders()
        {
            return await _db.Order
                .Where(w => w.Status == false)
                .Select(o => new OrderDto
                {
                    Id = o.Id,
                    CreateDate = o.CreateDate,
                    Deadline = o.Deadline,
                    Count = o.Count,
                    Price = o.Price,
                    TotalPrice = o.TotalPrice,
                    Status = o.Status,
                    Product_id = o.Product.Id
                })
                .ToListAsync();
        }

        public async Task<OrderDto?> create(OrderCreateDto order, Specification spec)
        {
            var result = await _db.Order.AddAsync(new Order
            {
                CreateDate = (DateTime) order.CreateDate,
                Deadline = (DateTime)order.Deadline,
                Count = (int)order.Count,
                Price = (int)order.Price,
                TotalPrice = (int)order.Count * (int)order.Price,
                Status = false,
                Product = spec

            });
            await _db.SaveChangesAsync();
            return new OrderDto
            {
                Id = result.Entity.Id,
                CreateDate = result.Entity.CreateDate,
                Deadline = result.Entity.Deadline,
                Count = result.Entity.Count,
                Price = result.Entity.Price,
                TotalPrice = result.Entity.TotalPrice,
                Status = result.Entity.Status,
                Product_id = spec.Id
            };
        }

        public async Task<OrderDto?> update(long id, OrderUpdateDto dto, Specification? spec)
        {

            var order = await findByIdWithFK(id);

            if (order != null)
            {
                if (dto.CreateDate != null) order.CreateDate = (DateTime)dto.CreateDate;
                if (dto.Deadline != null) order.Deadline = (DateTime)dto.Deadline;
                if (dto.Count != null) order.Count = (int)dto.Count;
                if (dto.Price != null) order.Price = (int)dto.Price;
                order.TotalPrice = order.Count * order.Price;
                if (dto.Status != null) order.Status = (bool)dto.Status;
                if (spec != null) order.Product = spec;
                var result = _db.Order.Update(order);
                await _db.SaveChangesAsync();
                return await findById(id);
            }
            else return null;
        }

        public async Task<long?> delete(Order order)
        {
            _db.Remove(order);
            await _db.SaveChangesAsync();
            return order.Id;
        }

    }
}

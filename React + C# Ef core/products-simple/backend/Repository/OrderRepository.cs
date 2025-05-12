using kis.DTO;
using kis.Entity;
using Microsoft.EntityFrameworkCore;

namespace kis.Repository
{
    public class OrderRepository 
    {
        // Репозиторий делает запросы в бд, по вызову из сервиса
        private readonly ApplicationContext _db;
        public OrderRepository(ApplicationContext db)
        {
            _db = db;
        }

        public async Task<List<OrderDto>?> get()
        {
            // таблицу Order в бд достать в виде списка
            // при этом нужно отобразить Product_id
            // для этого создаем объекты для каждой записи в бд
            return await _db.Order
                .Select(o => new OrderDto
                {
                    Id = o.Id,
                    CreateDate = o.CreateDate,
                    Deadline = o.Deadline,
                    Count = o.Count,
                    Price = o.Price,
                    FullPrice = o.FullPrice,
                    Product_id = o.Product.Id
                })
                .ToListAsync();
        }
        public async Task<Order?> findByIdWithFK(long id)
        {
            try
            {
                // из таблицы Order в бд достать запись, в которой совпадает id с выбранным
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
            { // из таблицы Order в бд достать запись, в которой совпадает id с выбранным
              // при этом нужно отобразить Product_id
              // для этого создаем объект
                return await _db.Order
               .Where(o => o.Id == id)
               .Select(o => new OrderDto
               {
                   Id = o.Id,
                   CreateDate = o.CreateDate,
                   Deadline = o.Deadline,
                   Count = o.Count,
                   Price = o.Price,
                   FullPrice = o.FullPrice,
                   Product_id = o.Product.Id
               })
               .SingleAsync();
            }
            catch (InvalidOperationException)
            {   // Ошибка возникает когда запись не найдена, возвращаем null
                return null;
            }
            
        }

        public async Task<OrderDto?> create(OrderCreateDto order, Specification spec)
        {

            var result = await _db.Order.AddAsync(new Order // создать запись в бд Specification, с полями из dto
            {
                CreateDate = (DateTime) order.CreateDate,
                Deadline = (DateTime)order.Deadline,
                Count = (int)order.Count,
                Price = (int)order.Price,
                FullPrice = (int)order.Count * (int)order.Price,
                Product = spec

            });
            await _db.SaveChangesAsync();// сохранить
            return new OrderDto // в качетсве результата показать созданную запись вместе с Product_id
            {
                Id = result.Entity.Id,
                CreateDate = result.Entity.CreateDate,
                Deadline = result.Entity.Deadline,
                Count = result.Entity.Count,
                Price = result.Entity.Price,
                FullPrice = result.Entity.FullPrice,
                Product_id = spec.Id
            };
        }

        public async Task<OrderDto?> update(long id, OrderCreateDto dto, Specification? spec)
        {
            // найти заказ для изменения
            var order = await findByIdWithFK(id);

            if (order != null) // если есть, то изменить указанные поля
            { // если не указали поле, то оно равно null -> измения не применятся
                if (dto.CreateDate != null) order.CreateDate = (DateTime)dto.CreateDate;
                if (dto.Deadline != null) order.Deadline = (DateTime)dto.Deadline;
                if (dto.Count != null) order.Count = (int)dto.Count;
                if (dto.Price != null) order.Price = (int)dto.Price;
                order.FullPrice = order.Count * order.Price; // пересчет FullPrice
                if (spec != null) order.Product = spec;
                var result = _db.Order.Update(order); // изменить
                await _db.SaveChangesAsync(); // сохранить
                return await findById(id); // показать результат
            }
            else return null;
        }

        public async Task<long?> delete(long id)
        {
            var order = await findByIdWithFK(id); // найти заказ
            if (order == null) return null;
            _db.Remove(order); // если есть, то удалить
            await _db.SaveChangesAsync(); // сохранить
            return order.Id; // показать что удалили
        }

    }
}

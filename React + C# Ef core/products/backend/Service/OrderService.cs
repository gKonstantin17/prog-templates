using kis.DTO.Order;
using kis.Entity;
using kis.Repository;

namespace kis.Service
{
    public class OrderService
    {
        private OrderRepository repository;
        private SpecRepository specRepository;
        public OrderService(OrderRepository repository, SpecRepository specRepository)
        {
            this.repository = repository;
            this.specRepository = specRepository;
        }

        public async Task<List<OrderWithProduct>?> get() => await repository.get();

        public async Task<(OrderDto?,string)> create(OrderCreateDto dto)
        {
            
            if (dto.CreateDate == null) dto.CreateDate = DateTime.UtcNow;
            if (dto.Deadline == null) dto.Deadline = dto.CreateDate.Value.AddDays(7);
            // create date раньше deadline
            if (!isCreateBeforeUpdate(dto.CreateDate.Value, dto.Deadline.Value))
                return (null, "Дата заказа должна быть раньше срока выполнения");

            if (dto.Count == null) dto.Count = 1;
            if (dto.Price == null) dto.Price = 100;
            
            var spec = await specRepository.findById((long)dto.Product_id);
            if (spec == null) return (null, "Товар не найден");

            var result = await repository.create(dto, spec);
            return (result, "Ok");
        }
        private bool isCreateBeforeUpdate(DateTime createDate, DateTime deadline)
        {
            if (createDate.CompareTo(deadline) < 0)
                return true;
            else
                return false;
        }

        public async Task<(OrderDto? Order, string? Error)> update(long id, OrderUpdateDto dto)
        {
            if ((dto.CreateDate != null) && (dto.Deadline != null))
                if (!isCreateBeforeUpdate(dto.CreateDate.Value, dto.Deadline.Value))
                    return (null, "Дата заказа должна быть раньше срока выполнения");
            Specification? spec = null;
            if (dto.Product_id != null)
            {
                spec = await specRepository.findById((long)dto.Product_id);
                if (spec == null)
                    return (null, "Товар не найден");
            }
                
            var result = await repository.update(id, dto, spec);

            return (result, null);
        }

        public async Task<long?> delete(long id)
        {
            var order = await repository.findByIdWithFK(id);
            if (order == null) return null;
            return await repository.delete(order);
        }
    }
}

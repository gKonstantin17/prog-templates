using kis.DTO;
using kis.Entity;
using kis.Repository;

namespace kis.Service
{
    public class OrderService
    {
        // Сервис получает запрос из контроллера, обрабатывает его
        // использует репозитории для запросов в бд
        private OrderRepository repository;
        private SpecRepository specRepository;
        public OrderService(OrderRepository repository, SpecRepository specRepository)
        {
            this.repository = repository;
            this.specRepository = specRepository;
        }

        public async Task<List<OrderDto>?> get() => await repository.get(); // сразу вызывает репозиторий, который достает список всех заказов

        public async Task<OrderDto?> create(OrderCreateDto dto)
        {
            // CreateDate по умолчанию - текущее время
            if (dto.CreateDate == null) dto.CreateDate = DateTime.UtcNow;
            // Deadline по умолчанию - +неделя от CreateDate
            if (dto.Deadline == null) dto.Deadline = dto.CreateDate.Value.AddDays(7);
            // CreateDate раньше deadline
            if (!isCreateBeforeUpdate(dto.CreateDate.Value, dto.Deadline.Value))
                return null;

            // Count по умолчанию - 1
            if (dto.Count == null) dto.Count = 1;
            // Price по умолчанию - 100
            if (dto.Price == null) dto.Price = 100;
            
            // есть ли товар из заказа в бд?
            var spec = await specRepository.findById((long)dto.Product_id);
            if (spec == null) return null;

            var result = await repository.create(dto, spec);
            return result;
        }
        private bool isCreateBeforeUpdate(DateTime createDate, DateTime deadline)
        {// проверка CreateDate раньше deadline
            if (createDate.CompareTo(deadline) < 0)
                return true;
            else
                return false;
        }

        public async Task<OrderDto?> update(long id, OrderCreateDto dto)
        {
            // проверка измения дат
            if ((dto.CreateDate != null) && (dto.Deadline != null))
                if (!isCreateBeforeUpdate(dto.CreateDate.Value, dto.Deadline.Value))
                    return null;//Дата заказа должна быть раньше срока выполнения
            
            Specification? spec = null;

            // если изменили товар заказа, проверка есть ли такой в бд
            if (dto.Product_id != null)
            {
                spec = await specRepository.findById((long)dto.Product_id);
                if (spec == null)
                    return null;//Товар не найден
            }
            
            // нахождение заказа и изменение происходит в репозитории
            var result = await repository.update(id, dto, spec);

            return result;// Всё ок
        }

        public async Task<long?> delete(long id) => await repository.delete(id); // сразу вызывает репозиторий, который удаляет заказ

    }
}

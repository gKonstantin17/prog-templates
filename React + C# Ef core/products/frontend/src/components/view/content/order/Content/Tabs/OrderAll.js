import {OrderApi} from "../../../../../utils/OrderApi";
import {useEffect} from "react";

function OrderAll() {
    const {data, error,loading, getAll} = OrderApi();
    useEffect( () => {
        getAll()
    },[getAll]);
    if (loading) return (<p>чел, грузится</p>)
    if (error) return (<h2>Ошибка: {error.message}</h2>)

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ru-RU') + ' ' + date.toLocaleTimeString('ru-RU', {hour: '2-digit', minute:'2-digit'});
    };
    const formatStatus = (orderStatus) => {
        if (orderStatus === true)
            return 'Выполнен'
        else return 'Активен'
    }
    const sortedData = [...data].sort((a, b) => {
        // 0 - 1 = -1 -> a
        return a.status - b.status;
    });
    // Статус заказа на основе deadline
    const getOrderStatus = (dataStatus) => {
        const status = dataStatus;
        return status === true ? 'Выполнен' : 'Активен';
    };
    return (
        <div className="orders-container">
            <div className="orders-header">
                <h2>Заказы</h2>

            </div>

            <div className="orders-table-container">
                <table className="spec-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Дата создания</th>
                        <th>Срок выполнения</th>
                        <th>Товар</th>
                        <th>Кол-во</th>
                        <th>Цена</th>
                        <th>Сумма</th>
                        <th>Статус</th>
                    </tr>
                    </thead>
                    <tbody>
                    {sortedData.map(order => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{formatDate(order.createDate)}</td>
                            <td>{formatDate(order.deadline)}</td>
                            <td>{order.product} (id:{order.product_id}) </td>
                            <td>{order.count}</td>
                            <td>{order.price} ₽</td>
                            <td>{order.totalPrice} ₽</td>

                            <td>{formatStatus(order.status)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className="orders-footer">
                <div className="orders-summary">
                    Всего заказов: {data.length} |
                    Активных: {data.filter(o => getOrderStatus(o.status) === 'Активен').length} |
                    Просроченных: {data.filter(o => getOrderStatus(o.status) === 'Выполнен').length}
                </div>
            </div>
        </div>
    );

}
export {OrderAll}
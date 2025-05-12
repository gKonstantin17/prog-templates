import {OrderApi} from "../../../../../utils/OrderApi";
import {useState} from "react";

function OrderChange() {
    const {data, error,loading, updateOrder} = OrderApi();
    const [orderId, setOrderId] = useState('');
    const [creatingOrder,setCreatingOrder] = useState({
        createDate: null,
        deadline: null,
        count: null,
        product_id: null,
        price: null,
        status: null,
    })


    const createSubmit = async (e) =>{
        e.preventDefault()
        try {
            await updateOrder(creatingOrder,orderId)
        } catch (err) {
            console.log("err:",err)
        }
    }
    const onChangeOrder = (e) => {
        const {name,value} = e.target;
        setCreatingOrder(prev => ({
            ...prev,
            [name]:value
        }))
    }
    return(<div>
        <h2>Изменить</h2>


        <form onSubmit={createSubmit}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id изменяемого заказа</div>
                    <div className='form-lable'>Дата заказа</div>
                    <div className='form-lable'>Срок выполнения</div>
                    <div className='form-lable'>Количество</div>
                    <div className='form-lable'>id Товара (из спецификации)</div>
                    <div className='form-lable'>Цена</div>
                    <div className='form-lable'>Статус</div>


                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name='orderId'
                           value={orderId}
                           onChange={(e) => setOrderId(e.target.value)}
                           placeholder='1'
                           required
                    />
                    <input className='form-input'
                        name="createDate"
                        value={creatingOrder.createDate}
                        onChange={onChangeOrder}
                        placeholder='ГГГГ-ММ-ДД'
                    />
                    <input className='form-input'
                        name="deadline"
                        value={creatingOrder.deadline}
                        onChange={onChangeOrder}
                        placeholder='ГГГГ-ММ-ДД'
                    />
                    <input className='form-input'
                        name="count"
                        value={creatingOrder.count}
                        onChange={onChangeOrder}
                        placeholder='1'
                    />
                    <input className='form-input'
                        name="product_id"
                        value={creatingOrder.product_id}
                        onChange={onChangeOrder}
                        placeholder='1'
                    />
                    <input className='form-input'
                        name="price"
                        value={creatingOrder.price}
                        onChange={onChangeOrder}
                        placeholder='100'
                    />
                    <input className='form-input'
                        name="status"
                        value={creatingOrder.status}
                        onChange={onChangeOrder}
                        placeholder='true(выполнен)/false(нет)'
                    />
                </div>
            </div>

            <button className='action-btn'>Изменить заказ</button>
        </form>

        {data &&
            <div className='form-result-wrapper'>
                <div className='form-result'>Заказ успешно создан</div>
                <div className='form-subresult'>id: {data.id}</div>
                <div className='form-subresult'>Дата заказа: {new Date(data.createDate).toLocaleString()}</div>
                <div className='form-subresult'>Срок выполнения: {new Date(data.deadline).toLocaleString()}</div>
                <div className='form-subresult'>Количество: {data.count}</div>
                <div className='form-subresult'>id товара: {data.product_id}</div>
                <div className='form-subresult'>Цена: {data.price}</div>
                <div className='form-subresult'>Сумма: {data.totalPrice}</div>
                <div className='form-subresult'>Статус: {data.status == false ? 'Выполняется' : 'Выполнен'}</div>

            </div>
            }
        {error && <div>{error}</div>}
    </div>)
}
export {OrderChange}
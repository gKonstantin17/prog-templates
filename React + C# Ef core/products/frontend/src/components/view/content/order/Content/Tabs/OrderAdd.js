import {OrderApi} from "../../../../../utils/OrderApi";
import {useState} from "react";

function OrderAdd() {
    const {data, error,loading, createOrder} = OrderApi();
    const [creatingOrder,setCreatingOrder] = useState({
        createDate: null,
        deadline: null,
        count: null,
        product_id: null,
        price: null
    })

    const createSubmit = async (e) =>{
        e.preventDefault()
        try {
            await createOrder(creatingOrder)
        } catch (err) {
            console.log("err:",err)
        }
    }
    const onChangeCreate = (e) => {
        const {name,value} = e.target;
        setCreatingOrder(prev => ({
            ...prev,
            [name]:value
        }))
    }
    return(<div>
        <h2>Добавить</h2>
        <form onSubmit={createSubmit}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>Дата заказа</div>
                    <div className='form-lable'>Срок выполнения</div>
                    <div className='form-lable'>Количество</div>
                    <div className='form-lable'>id Товара (из спецификации)</div>
                    <div className='form-lable'>Цена</div>

                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                        name="createDate"
                        value={creatingOrder.createDate}
                        onChange={onChangeCreate}
                        placeholder='ГГГГ-ММ-ДД'
                    />
                    <input className='form-input'
                        name="deadline"
                        value={creatingOrder.deadline}
                        onChange={onChangeCreate}
                        placeholder='ГГГГ-ММ-ДД'
                    />
                    <input className='form-input'
                        name="count"
                        value={creatingOrder.count}
                        onChange={onChangeCreate}
                        placeholder='1'
                    />
                    <input className='form-input'
                        name="product_id"
                        value={creatingOrder.product_id}
                        onChange={onChangeCreate}
                        placeholder='1'
                           required
                    />
                    <input className='form-input'
                        name="price"
                        value={creatingOrder.price}
                        onChange={onChangeCreate}
                        placeholder='100'
                    />
                </div>
            </div>



            <button className='action-btn'>Создать заказ</button>
        </form>

        {data &&
            <div className='form-result-wrapper'>
                <div className='form-result'>
                    Заказ успешно создан
                </div>
                <div className='form-subresult'>id: {data.id} </div>
                <div className='form-subresult'>Дата заказа: {new Date(data.createDate).toLocaleString()} </div>
                <div className='form-subresult'>Срок выполнения: {new Date(data.deadline).toLocaleString()} </div>
                <div className='form-subresult'>Количество: {data.count} </div>
                <div className='form-subresult'>id товара: {data.product_id} </div>
                <div className='form-subresult'>Цена: {data.price} </div>
                <div className='form-subresult'> Статус: {data.status==false ? 'выполняется' : 'выполнен'}</div>
            </div>
            }
        {error && <div>{error}</div>}
    </div>)
}
export {OrderAdd}
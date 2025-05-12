import {useState} from "react";
import {OrderApi} from "../../../../../utils/OrderApi";

function OrderDelete() {
    const { data, error, deleteOrder} = OrderApi();
    const [orderId, setOrderId] = useState('');
    const DeleteOnClick = async (e) => {
        e.preventDefault()
        if (orderId)
            await deleteOrder(orderId)
    }
    return(<div>
        <h2>Удалить</h2>
        <form onSubmit={DeleteOnClick}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id удаляемой записи</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name='id'
                           value={orderId}
                           onChange={(e) => setOrderId(e.target.value)}
                           placeholder='1'
                           required
                    />
                </div>
            </div>

            <button className='action-btn'>Удалить</button>
        </form>

        {error && <div>{error}</div>}
        {data &&
                <div className='form-result'>Запись удалена (id:{data})</div>

        }</div>)

}
export {OrderDelete}
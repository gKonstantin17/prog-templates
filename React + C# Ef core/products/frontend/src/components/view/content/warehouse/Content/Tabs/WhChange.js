import {WarehouseApi} from "../../../../../utils/WarehouseApi";
import {useState} from "react";

function WhChange() {
    const {data,error,changeWh} = WarehouseApi();
    const [changeData, setChangeData] = useState({
        number:null,
        productId:null
    })

    const onChangeInput = (e) => {
        const {name,value} = e.target;
        setChangeData(prev => ({
            ...prev,
            [name]:value
        }))
    }

    const changeOnClick = (e) => {
        e.preventDefault()
        changeWh(changeData)
    }
    const formattedDate = (date) =>  new Date(date).toLocaleString();
    return(<div>
        <h2>Изменить количество товара</h2>
        <form onSubmit={changeOnClick}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id товара</div>
                    <div className='form-lable'>изменение количества</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name='productId'
                           value={changeData.productId}
                           onChange={onChangeInput}
                           placeholder='1'
                           required
                    />
                    <input className='form-input'
                           name='number'
                           value={changeData.number}
                           onChange={onChangeInput}
                           placeholder='±1'
                           required
                    />
                </div>
            </div>


            <button className='action-btn' >Изменить</button>
        </form>


        {data && (<div className='form-result-wrapper'>
            <div className='form-result'>Результат</div>
            <div className='form-subresult'>id в истории записей:{data.id}</div>
            <div className='form-subresult'>id товара: {data.product_Id}</div>
            <div className='form-subresult'>количество: {data.count}</div>
            <div className='form-subresult'>Дата изменения:{formattedDate(data.updateDate)}</div>

        </div>)}
        {error && (<div>{error}</div>)}
    </div>)
}
export {WhChange}
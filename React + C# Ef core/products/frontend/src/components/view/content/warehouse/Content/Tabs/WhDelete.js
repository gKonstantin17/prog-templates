import {WarehouseApi} from "../../../../../utils/WarehouseApi";
import {useState} from "react";

function WhDelete() {
    const {data,error,deleteFromHistory} = WarehouseApi();
    const [whId,setWhId] = useState(null);
    const deleteOnClick = (e) => {
        e.preventDefault()
        deleteFromHistory(whId)
    }
    return(<div>
        <h2>Удалить из истории изменений</h2>
        <form onSubmit={deleteOnClick}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id записи</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name='whId'
                           value={whId}
                           onChange={(e) => setWhId(e.target.value)}
                           placeholder='1'
                           required
                    />
                </div>
            </div>

            <button className='action-btn'  >Удалить</button>
        </form>

        {data && (<div className='form-result'>Запись удалена (id:{data})</div>)}
        {error && (<div>{error}</div>)}
    </div>)
}
export {WhDelete}
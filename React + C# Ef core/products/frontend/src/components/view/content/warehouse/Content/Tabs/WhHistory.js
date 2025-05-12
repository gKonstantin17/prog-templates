import {WarehouseApi} from "../../../../../utils/WarehouseApi";
import {useState} from "react";

function WhHistory() {
    const {data,error,historyChanges} = WarehouseApi();
    const [whId, setWhId] = useState(null);
    const showHistory =(e) => {
        e.preventDefault()
        historyChanges(whId);
    }
    const formattedDate = (date) =>  new Date(date).toLocaleString();
    return(<div className='wh-all-container'>
        <h2>История изменений</h2>
        <form onSubmit={showHistory}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id товара</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name='whId'
                           value={whId}
                           onChange={(e) => {setWhId(e.target.value)}}
                           placeholder='1'
                           required
                    />
                </div>
            </div>

            <button className='action-btn' >Посмотреть историю</button>
        </form>


        {data && (<div>

                <table className='spec-table'>
                    <thead><tr>
                        <th>Id</th>
                        <th>Id товара</th>
                        <th>Количество</th>
                        <th>Дата изменения</th>
                    </tr></thead>
                    <tbody>
                    {data.map(wh => (
                        <tr key={wh.id}>
                            <td>{wh.id}</td>
                            <td>{wh.product_Id}</td>
                            <td>{wh.count}</td>
                            <td>{formattedDate(wh.updateDate)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>

        </div>)}
        {error && (<div>{error}</div>)}
    </div>)

}
export {WhHistory}
import {SpecApi} from "../../../../../utils/SpecApi";
import {useState} from "react";

function SpecDelete() {
    const {data, error, deleteSpec} = SpecApi();
    const [specId, setSpecId] = useState(null);
    const DeleteOnClick = async (e) => {
        e.preventDefault()
        if (specId)
            await deleteSpec(specId)
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
                           value={specId}
                           onChange={(e) => setSpecId(e.target.value)}
                           placeholder='1'
                           required
                    />
                </div>
            </div>

            <button className='action-btn' >Удалить</button>
        </form>

        {error && <div>{error}</div>}
        {data && <div className='form-result'>Запись удалена (id:{data})</div>}
    </div>)
}
export {SpecDelete}
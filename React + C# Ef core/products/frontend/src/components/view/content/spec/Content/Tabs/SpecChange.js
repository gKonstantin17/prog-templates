import {SpecApi} from "../../../../../utils/SpecApi";
import {useState} from "react";

function SpecChange() {
    const { data, error, changeSpec  } = SpecApi();
    const [specChangeData, setSpecChangeData] = useState({
        level: null,
        name: null,
        count:null,
        parent_id:null
    })
    const [SpecId, setSpecId] = useState('');
    const [errorMessages, setErrorMessages] = useState([]);
    const validateForm = () => {
        const errors = [];
        if (!specChangeData.level && !specChangeData.name && !specChangeData.count) {
            errors.push("Заполните хотя бы одно поле.");
        }
        return errors;
    }
    const changeSubmit = async (e) => {
        e.preventDefault();
        const errors = validateForm();
        if (errors.length > 0) {
            setErrorMessages(errors);
            return; // Прерываем выполнение, если есть ошибки
        }
        try {
            await changeSpec(specChangeData,SpecId);
        } catch (err) {
            console.log("err:", err.response.data)
        }
    }

    const onChangeSpec = (e) => {
        const {name,value} = e.target;
        setSpecChangeData(prev => ({
            ...prev,
            [name]:value
        }))
    }
    return(<div>
        <h2>Изменить</h2>
        <form onSubmit={changeSubmit}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id изменяемой записи</div>
                    <div className='form-lable'>Уровень (от 1)</div>
                    <div className='form-lable'>Название</div>
                    <div className='form-lable'>Количество</div>
                    <div className='form-lable'>Родительский компонент (если есть)</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name="id"
                           value={SpecId}
                           onChange={(e) => setSpecId(e.target.value)} // Преобразуем в число
                           placeholder='1'
                           required
                    />
                    <input className='form-input'
                        name="level"
                        value={specChangeData.level}
                        onChange={onChangeSpec}
                        placeholder='1'
                    />
                    <input className='form-input'
                        name="name"
                        value={specChangeData.name}
                        onChange={onChangeSpec}
                        placeholder='Название'
                    />
                    <input className='form-input'
                        name="count"
                        value={specChangeData.count}
                        onChange={onChangeSpec}
                        placeholder='1'
                    />
                    <input className='form-input'
                        name="parent_id"
                        value={specChangeData.parent_id}
                        onChange={onChangeSpec}
                    />
                </div>
            </div>

            <button className='action-btn'>Изменить</button>
        </form>
        {data && (
            <div className='form-result-wrapper'>
                <div className='form-result'>  Результат:</div>
                <div className='form-subresult'>{data.name} (x{data.level}{data.parent_id == null ? '' : ',Parent_id:'+data.parent_id})</div>
            </div>

        )}
        {errorMessages && <div>{errorMessages}</div>}
        {error && <div>{error}</div>}
    </div>)
}
export {SpecChange}
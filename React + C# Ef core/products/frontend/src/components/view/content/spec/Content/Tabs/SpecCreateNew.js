import {SpecApi} from "../../../../../utils/SpecApi";
import {useState} from "react";

function SpecCreateNew() {
    const { data, error, createSpec  } = SpecApi();
    const [specCreateData, setSpecCreateData] = useState( {
        level: null,
        name: null,
        count:null,
        parent_id:null
    })
    const createSubmit = async (e) => {
        e.preventDefault();
        try {
            await createSpec(specCreateData);
        } catch (err) {
            console.log("err:", err.response.data)
        }
    }

    const onChangeCreate = (e) => {
        const {name,value} = e.target;
        setSpecCreateData(prev => ({
            ...prev,
            [name]:value
        }))
    }
    return(<div>
        <h2>Добавить</h2>
        <form onSubmit={createSubmit}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>Уровень (от 1)</div>
                    <div className='form-lable'>Название</div>
                    <div className='form-lable'>Количество</div>
                    <div className='form-lable'>Родительский компонент (если есть)</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           name="level"
                           value={specCreateData.level}
                           onChange={onChangeCreate}
                           placeholder='1'
                           required
                    />
                    <input className='form-input'
                        name="name"
                        value={specCreateData.name}
                        onChange={onChangeCreate}
                        placeholder='Название'
                           required
                    />
                    <input className='form-input'
                        name="count"
                        value={specCreateData.count}
                        onChange={onChangeCreate}
                        placeholder='1'
                           required
                    />
                    <input className='form-input'
                        name="parent_id"
                        value={specCreateData.parent_id}
                        onChange={onChangeCreate}
                    />
                </div>
            </div>

            <button className='action-btn'>Добавить</button>
        </form>
        {data && <div className='form-result' >Создан компонент спецификации (id: {data})</div>}
    </div>)
}
export {SpecCreateNew}
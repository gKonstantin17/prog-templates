import {SpecApi} from "../../../../../utils/SpecApi";
import {useState} from "react";
function SpecCount() {
    const { data, error, countComponents  } = SpecApi();
    const [inputValue, setInputValue] = useState(null); // Состояние для input

    // выполнение запроса
    const countOnClick = (e) => {
        e.preventDefault()
        if (inputValue)
            countComponents(inputValue)
    }

    return(<div>
        <h2>Посчитать компоненты</h2>
        <form onSubmit={countOnClick}>
            <div className='form-container'>
                <div className='form-labels'>
                    <div className='form-lable'>id компонента</div>
                </div>
                <div className='form-inputs'>
                    <input className='form-input'
                           value={inputValue}
                           onChange={(e) => setInputValue(e.target.value)}
                           type="number"
                           placeholder='1'
                           required
                    />
                </div>
            </div>
            <button className='action-btn' >Найти</button>
        </form>

        {data && (
            <div className="spec-count-result">
                <h2>
                    {data.name} (id: {data.id}) состоит из:
                </h2>
                <table className="spec-table">
                    <thead>
                    <tr>
                        <th>Название</th>
                        <th>ID</th>
                        <th>Количество</th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.specifications.map((component) => (
                        <tr key={component.id}>
                            <td>{component.name}</td>
                            <td>{component.id}</td>
                            <td>{component.count}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        )}

        {error && <div>{error}</div>}
    </div>)
}
export {SpecCount}
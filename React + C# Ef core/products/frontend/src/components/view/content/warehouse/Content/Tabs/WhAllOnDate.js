import {useEffect, useState} from "react";
import {WarehouseApi} from "../../../../../utils/WarehouseApi";

function WhAllOnDate() {
    const { data, error, loading, getByDate } = WarehouseApi();
    const [choosenDate, setChoosenDate] = useState(''); // выбранная дата

    useEffect(() => { // при загрузке компонента
        const offset = new Date().getTimezoneOffset() * 60000;
        const currentDate = new Date(new Date() - offset).toISOString();
        // Запрашиваем на текущее время (локальное)
        getByDate(currentDate);
    }, []);


    // отображать в формате ГГГГ-ММ-ДД
    const formattedDate = (date) =>  new Date(date).toLocaleString();

    useEffect(() => { // при выборе даты
        if (choosenDate) {
            getByDate(choosenDate);
        }
    }, [choosenDate]);
    const showOnChoosenDate = (e) => {
        const selected = new Date(e.target.value).toISOString().split('T')[0];
        setChoosenDate(selected);
    }

    if (loading) return <p>Загрузка данных...</p>;
    return (<div className='wh-all-container'>
        <div className='form-container'>
            <div className='form-labels'>
                <div className='form-lable'>Фильтр по дате</div>
            </div>
            <div className='form-inputs'>
                <input className='form-input'
                       type="date"
                       name="date"
                       value={choosenDate}
                       onChange={showOnChoosenDate}
                />
            </div>
        </div>

        {data && (
           <table className='spec-table'>
                <thead><tr>
                    <th>Id</th>
                    <th>Товар</th>
                    <th>Количество</th>
                    <th>Дата изменения</th>
                </tr></thead>

                <tbody>
               {data.map(product => (
                   <tr key={product.id}>
                       <td>{product.id}</td>
                       <td>{product.product} (id:{product.product_Id})</td>
                       <td>{product.count}</td>
                       <td>{formattedDate(product.updateDate)}</td>
                   </tr>
               ))}
               </tbody>
            </table>
       )}
        {error && (<div>{error}</div>)}
    </div>)
};

export { WhAllOnDate };
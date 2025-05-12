import {WarehouseApi} from "../../../../../utils/WarehouseApi";

function WhCheck() {
    const {data,error, checkComponents } = WarehouseApi();
    const checkOnClick = () => {
        checkComponents()
    }
    return(<div className='wh-all-container'>
        <h2>Проверить наличие компонентов на незавершенные заказы</h2>

        <button className='action-btn' onClick={checkOnClick}>Проверить</button>
        {data && <div>
            <table className='spec-table'>
                <thead>
                <tr>
                    <th>Товар</th>
                    <th>Требуется</th>
                    <th>В наличии</th>
                    <th>Нехватает</th>
                    <th>Статус</th>
                </tr>
                </thead>
                <tbody>
                {data.map(product => (
                <tr>
                    <td>{product.product} (id: {product.productId})</td>
                    <td>{product.result.required}</td>
                    <td>{product.result.available}</td>
                    <td>{product.result.missing}</td>
                    <td>{product.result.status}</td>
                </tr>
                ))}
                </tbody>

            </table>

        </div>}
    </div>)
}
export {WhCheck}
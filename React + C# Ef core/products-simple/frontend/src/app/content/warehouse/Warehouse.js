import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {WarehouseApi} from "./WarehouseApi";
import {useEffect, useState} from "react";
import {WarehouseChange} from "./WarehouseChange";
import {WarehouseHistory} from "./WarehouseHistory";
import {WarehouseDeficit} from "./WarehouseDeficit";
import {WarehouseDelete} from "./WarehouseDelete";

function Warehouse() {
    // выполняет get запрос и отображает таблицу с товарами склада на данный момент
    // также с этой страницы запускаются окна для изменения, удаления товаров и подсчета дефицита
    // все запросы хранятся в WarehouseApi.js

    // для get запроса
    const {data,error,loading,get} = WarehouseApi();
    // запрос таблицы на текущее время
    const getNow = () => {
        const offset = new Date().getTimezoneOffset() * 60000;
        const currentDate = new Date(new Date() - offset).toISOString();
        // Запрашиваем на текущее время (локальное)
        get(currentDate);
    }
    useEffect(() => { // при загрузке компонента
        getNow()
    }, []);
    const [choosenDate, setChoosenDate] = useState(''); // выбранная дата
    const showOnChoosenDate = (e) => { // при выборе даты, будет изменятся поле choosenDate
        const selected = new Date(e.target.value).toISOString().split('T')[0];
        setChoosenDate(selected);
    }
    useEffect(() => { // при изменении choosenDate будет обновляться таблица
        if (choosenDate) {
            get(choosenDate);
        }
    }, [choosenDate]);

    const [selectedProductId, setSelectedProductId] = useState(null);
    const [selectedId,setSelectedId] = useState(null);
    const [changeModalOpen, setChangeModalOpen] = useState(false);
    const [historyModalOpen, setHistoryModalOpen] = useState(false);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [deficitModalOpen, setDeficitModalOpen] = useState(false);


    if (error) return (<div>Ошибка: {error.message}</div>)
    if (loading) return (<div>Загрузка данных</div>)
    return(<div>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <input className='form-input'
                   type="date"
                   name="date"
                   value={choosenDate}
                   onChange={showOnChoosenDate}
            />
            <button onClick={() => {
                setSelectedProductId(null)
                setChangeModalOpen(true);
            }}>
                ➕</button>
            <button onClick={() => {
                setDeficitModalOpen(true);
            }}>
                🔎</button>
        </div>
        {/*Модальные окна*/}
        <WarehouseChange
        open={changeModalOpen}
        onClose={() => setChangeModalOpen(false)}
        reload={getNow}
        product={selectedProductId}
        />
        <WarehouseHistory
        open={historyModalOpen}
        onClose={() => setHistoryModalOpen(false)}
        product={selectedProductId}
        />
        <WarehouseDelete
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        reload={getNow}
        id={selectedId}
        />
        <WarehouseDeficit
            open={deficitModalOpen}
            onClose={() => setDeficitModalOpen(false)}
        />
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Id</TableCell>
                        <TableCell>Id товара</TableCell>
                        <TableCell>Количество</TableCell>
                        <TableCell>Дата изменения</TableCell>
                    </TableRow>
                </TableHead>
                {Array.isArray(data) && <TableBody>
                    {data.map((whProduct, idx) => (
                        <TableRow key={idx}>
                            <TableCell>{whProduct.id}</TableCell>
                            <TableCell>{whProduct.product_Id}</TableCell>
                            <TableCell>{whProduct.count}</TableCell>
                            <TableCell>{new Date(whProduct.updateDate).toLocaleString()}</TableCell>
                            <TableCell>
                                <button onClick={() => {
                                    setSelectedProductId(whProduct.product_Id)
                                    setChangeModalOpen(true);
                                }}>
                                    ✏️
                                </button>
                            </TableCell>
                            <TableCell>
                                <button onClick={() => {
                                    setSelectedProductId(whProduct.product_Id)
                                    setHistoryModalOpen(true);
                                }}>
                                    📜
                                </button>
                            </TableCell>
                            <TableCell>
                                <button onClick={() => {
                                    setSelectedId(whProduct.id)
                                    setDeleteModalOpen(true)
                                }}>
                                    🗑️
                                </button>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>}
            </Table>
        </TableContainer>
    </div>)
}
export {Warehouse}
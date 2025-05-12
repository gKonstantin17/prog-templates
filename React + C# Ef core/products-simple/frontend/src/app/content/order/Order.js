import {
    Table,TableBody,TableCell,TableContainer, TableHead, TableRow, Paper,
} from '@mui/material';
import {formatDate} from "../../App";
import {OrderApi} from "./OrderApi";
import {useEffect, useState} from "react";
import {OrderCreate} from "./OrderCreate";
import {OrderUpdate} from "./OrderUpdate";
import {OrderDelete} from "./OrderDelete";

function Order() {
    // выполняет get запрос и отображает таблицу с заказами
    // также с этой страницы запускаются окна для создания, изменения, удаления заказов
    // все запросы хранятся в OrderApi.js

    // для get запроса
    const {data,error,loading,getAll} = OrderApi();
    useEffect( () => {  // выполнение запроса при отображении Order
        getAll()
    },[]);

    const [selectedOrderId, setSelectedOrderId] = useState(null); // выбор order
    const [createModalOpen, setCreateModalOpen] = useState(false); // открыть создание
    const [updateModalOpen, setUpdateModalOpen] = useState(false); // открыть изменение
    const [deleteModalOpen, setDeleteModalOpen] = useState(false); // открыть удаление
    if (error) return (<div>Ошибка: {error.message}</div>)
    if (loading) return (<div>Загрузка данных</div>)
    return(<div>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <button onClick={() => setCreateModalOpen(true)}>➕</button>
        </div>
        {/* Модальные окна*/}
        <OrderCreate
            open={createModalOpen}
            onClose={() => setCreateModalOpen(false)}
            reload={getAll}
        />
        <OrderUpdate
            open={updateModalOpen}
            onClose={() => setUpdateModalOpen(false)}
            reload={getAll}
            id={selectedOrderId}
        />
        <OrderDelete
            open={deleteModalOpen}
            onClose={() => setDeleteModalOpen(false)}
            reload={getAll}
            id={selectedOrderId}
        />
        {/* Таблица с заказами */}
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Id</TableCell>
                        <TableCell>Дата создания</TableCell>
                        <TableCell>Дата завершнения</TableCell>
                        <TableCell>Количество</TableCell>
                        <TableCell>Цена</TableCell>
                        <TableCell>Итоговая цена</TableCell>
                        <TableCell>Id товара</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data.map((order, idx) => (
                        <TableRow key={idx}>
                            <TableCell>{order.id}</TableCell>
                            <TableCell>{formatDate(order.createDate)}</TableCell>
                            <TableCell>{formatDate(order.deadline)}</TableCell>
                            <TableCell>{order.count}</TableCell>
                            <TableCell>{order.price} ₽</TableCell>
                            <TableCell>{order.fullPrice} ₽</TableCell>
                            <TableCell>{order.product_id}</TableCell>
                            <TableCell>
                                <button onClick={() => {
                                    setSelectedOrderId(order.id)
                                    setUpdateModalOpen(true);}}>
                                    ✏️
                                </button>
                            </TableCell>
                            <TableCell>
                                <button onClick={() => {
                                    setSelectedOrderId(order.id)
                                    setDeleteModalOpen(true)
                                }}>
                                    🗑️
                                </button>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    </div>)
}
export {Order}
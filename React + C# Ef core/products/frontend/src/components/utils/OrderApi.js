import {env} from "./env";
import { useState} from "react";
import axios from "axios";


function OrderApi() {
    const BASE_URL = env.RESOURCE_URL+"/order";
    const [data,setData] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);

    const getAll = (async () => {
        try {
            setError(null);
            const response = await axios.get(`${BASE_URL}/get`);
            setData(response.data);
            return response.data;
        } catch (err) {
            setError(err);
            throw err;
        }
        finally {
            setLoading(false);
        }
    });

    const createOrder = (async (order) => {
        try {
            setError(null);
            const response = await axios.post(`${BASE_URL}/create`,
                {
                    createDate: order.createDate,
                    deadline: order.deadline,
                    count: order.count,
                    product_id: order.product_id,
                    price: order.price
                },
                {
                    headers: {'Content-Type': 'application/json'},
                    validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
                });
            if (response.status >= 400) { // получение сообщения из backend
                const errorMessage =response.data;
                setError(errorMessage);
                setData(null);
                return null;
            }
            setData(response.data);
            return response;
        } catch (err) {
            setError(err);
            return null;
        }
    })

    const updateOrder = (async(order,id) => {
        try {
            setError(null);
            const response = await axios.put(`${BASE_URL}/update/${id}`,
                {
                    createDate: order.createDate,
                    deadline: order.deadline,
                    count: order.count,
                    price: order.price,
                    status: order.status,
                    product_id: order.product_id
                },
                {
                    headers: {'Content-Type': 'application/json'},
                    validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
                });
            if (response.status >= 400) { // получение сообщения из backend
                const errorMessage =response.data;
                setError(errorMessage);
                setData(null);
                return null;
            }
            setData(response.data);
            return response;
        } catch (err) {
            setError(err);
            return null;
        }
    })

    const deleteOrder = (async (id) => {
        try {
            setError(null);
            const response = await axios.delete(`${BASE_URL}/delete/${id}`,
                {validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
        })
        if (response.status >= 400) { // получение сообщения из backend
            const errorMessage =response.data;
            setError(errorMessage);
            setData(null);
            return null;
        }
        setData(response.data);
        return response;
    } catch (err) {
        setError(err);

        return null;
    }
})
    return {data,loading,error,getAll,createOrder,updateOrder,deleteOrder}
}
export {OrderApi}
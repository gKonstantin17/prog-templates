import {env} from "../../App";
import {useState} from "react";
import axios from "axios";

function OrderApi() {
    // Запросы для Order
    const URL = `${env.Backend}/order`;
    const [data,setData] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);

    // запрос на все заказы
    const getAll = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await axios.get(`${URL}/get`);
            setData(response.data);
            return response.data;
        } catch (e) {
            setError(e)
        } finally {
            setLoading(false)
        }
    }

    // запрос на создание
    const createOrder = async (order) => {
        try {
            setError(null);
            const response = await axios.post(`${URL}/create`,
                {
                    createDate: order.createDate,
                    deadline: order.deadline,
                    count: order.count,
                    product_id: order.product_id,
                    price: order.price
                },
                {headers: {'Content-Type': 'application/json'},})


            setData(response.data)
            return response.data
        } catch (e) {
            setError(e)
        }
    }

    // запрос на изменение
    const update = async (order, id) => {
        try {
            setError(null);
            const response = await axios.put(`${URL}/update/${id}`,
                {
                    createDate: order.createDate,
                    deadline: order.deadline,
                    count: order.count,
                    price: order.price,
                    product_id: order.product_id
                },
                {headers: {'Content-Type': 'application/json'}})
            setData(response.data)
            return response.data
        } catch (e) {
            setError(e)
        }
    }

    // запрос на удаление
    const deleteOrder = async (id) => {
        try {
            setError(null);
            const response = await axios.delete(`${URL}/delete/${id}`)
            return response.data
        } catch (e) {
            setError(e)
        }
    }

    return {
        data, // результат запроса
        error, // ошибка запроса
        loading, // статус запроса (при открытии компонента)
        getAll,  // запрос на все заказы
        createOrder, // запрос на создание
        update, // запрос на изменение
        deleteOrder // запрос на удаление
    }
}
export {OrderApi}




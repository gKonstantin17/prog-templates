import {env} from "../../App";
import {useState} from "react";
import axios from "axios";

function ProductApi() {
    // Запросы для Product (Specification)
    const URL = `${env.Backend}/product`;
    const [data,setData] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);

    // запрос на все товары
    const getAll = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await axios.get(`${URL}/get`)
            setData(response.data)
            return response.data
        } catch (e) {
            setError(e)
        } finally {
            setLoading(false)
        }
    }

    // запрос на создание
    const create = async (product,level,parent_id) => {
        console.log('parent_id:',parent_id)
        try {
            setError(null);
            const response = await axios.post(`${URL}/create`,
                {
                    level: level,
                    name: product.name,
                    count: product.count,
                    parent_id: parent_id
                },
                {headers: {'Content-Type': 'application/json'}})
            setData(response.data)
            return response.data
        } catch (e) {
            setError(e)
        }
    }

    // запрос на изменение
    const update = async (product,id) => {
        try {
            setError(null);
            const response = await axios.put(`${URL}/update/${id}`,
                {
                    level: product.level,
                    name: product.name,
                    count: product.count,
                    parent_id: product.parent_id
                },
                {headers: {'Content-Type': 'application/json'}})
            setData(response.data)
            return response.data
        } catch (e) {
            setError(e)
        }
    }
    // запрос на удаление
    const deleteProduct = async (id) => {
        try {
            setError(null);
            const response = await axios.delete(`${URL}/delete/${id}`)
            setData(response.data)
            return response.data
        } catch (e) {
            setError(e)
        }
    }

    return {data, // результат запроса
            error, // ошибка запроса
        loading, // статус запроса (при открытии компонента)
        getAll, // запрос на все заказы
        create, // запрос на создание
        update, // запрос на изменение
        deleteProduct // запрос на удаление
    }
}
export {ProductApi}
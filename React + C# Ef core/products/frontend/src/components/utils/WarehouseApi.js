import {env} from "./env";
import {useState} from "react";
import axios from "axios";

function WarehouseApi() {
    const BASE_URL = env.RESOURCE_URL+'/warehouse';
    const [data,setData] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);

    const getByDate = async (date) => {
        try {
            setError(null);
            setLoading(true);
            const response = await axios.post(
                `${BASE_URL}/get-by-date`,
                 `"${date}"` ,  // Тело запроса с датой
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
        } finally {
            setLoading(false);
        }
    };

    const checkComponents = async  () => {
        try {
            setError(null);
            const response = await axios.get(`${BASE_URL}/check-products`,
                {
                    validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
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
            setError(err)
            return null;
        }
    }

    const changeWh = async (changeData) => {
        try {
            setError(null)
            const response = await axios.post(`${BASE_URL}/change`,
                {
                    number: changeData.number,
                    productId: changeData.productId
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
    }

    const historyChanges = async (id) => {
        try {
            setError(null);
            const response = await axios.get(`${BASE_URL}/get-history/${id}`);
            setData(response.data);
            return response;
        } catch (err) {
            setError(err);
            return null;
        }
    }

    const deleteFromHistory = async (id) => {
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
    }
    return {data,
        error,
        loading,
        getByDate,
        changeWh,
        historyChanges,
        deleteFromHistory,
        checkComponents
        }
}
export {WarehouseApi}
import {env} from "../../App";
import {useState} from "react";
import axios from "axios";

function WarehouseApi() {
    const URL = `${env.Backend}/warehouse`;
    const [data,setData] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);
    const get = async (date) => {
        try {
            setError(null);
            setLoading(true);
            const response = await axios.post(
                `${URL}/get-by-date`,
                `"${date}"` ,  // Тело запроса с датой
                {headers: {'Content-Type': 'application/json'}});
            setData(response.data);
            return response.data;
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
            const response = await axios.get(`${URL}/check-products`)
            setData(response.data);
            return response.data;
        } catch (err) {
            setError(err)
            return null;
        }
    }

    const change = async (changeData) => {
        try {
            setError(null)
            const response = await axios.post(`${URL}/change`,
                {
                    number: changeData.number,
                    productId: changeData.productId
                },
                {headers: {'Content-Type': 'application/json'}});
            setData(response.data);
            return response.data;
        } catch (err) {
            setError(err);
            return null;
        }
    }

    const historyChanges = async (id) => {
        try {
            setError(null);
            const response = await axios.get(`${URL}/get-history/${id}`);
            setData(response.data);
            return response.data;
        } catch (err) {
            setError(err);
            return null;
        }
    }

    const deleteFromHistory = async (id) => {
        try {
            setError(null);
            const response = await axios.delete(`${URL}/delete/${id}`)
            return response.data;
        } catch (err) {
            setError(err);

            return null;
        }
    }
    return {data,
        error,
        loading,
        get,
        change,
        historyChanges,
        deleteFromHistory,
        checkComponents
    }
}
export {WarehouseApi}
import axios from "axios";
import {env} from "./env";
import {useCallback, useState} from "react";

export function SpecApi() {
    const BASE_URL = env.RESOURCE_URL+"/spec";
    const [data,setData] = useState(null);
    const [loading,setLoading] = useState(true);
    const [error,setError] = useState(null);


    // Для SpecViewAll
    const getAll = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await axios.get(`${BASE_URL}/get-all-hierarchy`);
            setData(response.data);
            return response.data;
        } catch (err) {
            setError(err);
            throw err;
        }
        finally {
            setLoading(false);
        }
    },[]);

    // Для SpecCount
    const countComponents = useCallback(async (id) => {
        try {
            setError(null);
            const response = await axios.post(
                `${BASE_URL}/count-components`,
                id,
                {
                    headers: {'Content-Type': 'application/json'},
                    validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
                }
            );
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

    // для SpecCreateNew
    const createSpec = useCallback(async (spec) => {
        try {
            setError(null);
            const response = await axios.post(`${BASE_URL}/create`,
                {
                    level: spec.level,
                    name: spec.name,
                    count: spec.count,
                    parent_id: spec.parent_id
                },
                {
                    headers: {'Content-Type': 'application/json'},
                    validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
                }
            );
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

    const changeSpec = (async (spec,id) => {
        try {
            setError(null);
            const response = await axios.put(`${BASE_URL}/update/${id}`,
                {
                    level: spec.level,
                    name: spec.name,
                    count: spec.count,
                    parent_id: spec.parent_id
                },
                {
                    headers: {'Content-Type': 'application/json'},
                    validateStatus: (status) => status < 500 // Обрабатываем 400-е статусы
                }
            );
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

    const deleteSpec = (async (id) => {
        try {
            setError(null);
            const response = await axios.delete(`${BASE_URL}/delete/${id}`, {
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
            setError(err);

            return null;
        }
    })
    return {data, // результат запроса
        loading, // статус выполнения запроса
        error,  // ошибки запроса
        getAll,// получить всё данные в виде дерева
        countComponents, // получить список компонентов
        createSpec, // создать запись в specilisation
        changeSpec, // изменить запись
        deleteSpec // удалить запись
    };
}
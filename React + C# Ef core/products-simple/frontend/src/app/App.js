import './style.css';
import Box from '@mui/material/Box';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import TabContext from '@mui/lab/TabContext';
import TabPanel from '@mui/lab/TabPanel';
import {useState} from "react";
import {Product} from "./content/product/Product";
import {Order} from "./content/order/Order";
import {Warehouse} from "./content/warehouse/Warehouse";
export function App() {
    // отображает 3 вкладки (Товары, Заказы, Склад)
    // при переключении активируются компоненты
    // Product, Order, Warehouse

    const [value, setValue] = useState('1'); // Активная вкладка
    // Обработчик переключения вкладок
    const handleChange = (event, newValue) => {
        setValue(newValue);
    };
    return (
        <Box sx={{ width: '100%', typography: 'body1' }}>
            <TabContext value={value}>
                <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                    <Tabs onChange={handleChange} value={value}>
                        <Tab label="Товары" value="1" />
                        <Tab label="Заказы" value="2" />
                        <Tab label="Склад" value="3" />
                    </Tabs>
                </Box>
                <TabPanel value="1">
                    <Product/>
                </TabPanel>
                <TabPanel value="2">
                    <Order/>
                </TabPanel>
                <TabPanel value="3">
                    <Warehouse/>
                </TabPanel>
            </TabContext>
        </Box>
    );
}

// Общие функции и константы проекта

// преставление даты в читаемом виде
export function formatDate(date) {
    return new Date(date).toLocaleDateString();
}

// объект с url-путями
export const env = {
    //Backend:"http://localhost:5144/api",
    // или локальный ip
    Backend:"http://192.168.145.92:5144/api",
}
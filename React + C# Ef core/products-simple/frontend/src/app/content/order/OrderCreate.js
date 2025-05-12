import {OrderApi} from "./OrderApi";
import {
    DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog, Box,
} from '@mui/material';
import {useState} from "react";
function OrderCreate({open,onClose, reload}) {
    // окно создания заказа, использует OrderApi
    const {createOrder} = OrderApi();

    // newOrder - объект с полями для создания заказа
    const [newOrder, setNewOrder] = useState({
        createDate: null,
        deadline: null,
        count: null,
        product_id: null,
        price:null
    });

    // при изменении поля ввода, сохранять в newOrder
    const handleChange = (e) => {
        const { name, value } = e.target;
        setNewOrder(prev => ({
            ...prev,
            [name]: value
        }));
        setErrors(prev => ({...prev, [name]: false}));
    };

    // по нажатию Создать
    const handleSubmit = async () => {
        if (!validate()) return; // проверить данные
        try {
            await createOrder(newOrder);    // создать
            onClose();                      // закрыть окно
            setNewOrder({             // очистить поля
                createDate: null,
                deadline: null,
                count: null,
                product_id: null,
                price:null
            });
            reload()                        // обновить таблицу со всеми заказами
        } catch (err) {
            console.error('Error creating order:', err);
        }
    };



    // есть ли ошибки при проверке полей
    const [errors, setErrors] = useState({
        count: false,
        product_id: false,
        price: false,
        createDate: false,
        deadline: false
    });

    // проверка полей
    const validate = () => {
        const newErrors = {
            count: !newOrder.count || newOrder.count < 0,
            product_id: !newOrder.product_id || newOrder.product_id < 0,
            price: !newOrder.price || newOrder.price < 0,
            deadline:(
                newOrder.createDate &&
                newOrder.deadline &&
                new Date(newOrder.deadline) < new Date(newOrder.createDate)
            )
        };
        setErrors(newErrors);
        return !Object.values(newErrors).some(Boolean);
    };

    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Создать новый заказ</DialogTitle>
            <DialogContent>
                <Box component="form"
                     noValidate
                     autoComplete="off"
                    sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>

                    <TextField
                        label="Количество"
                        name="count"
                        value={newOrder.count}
                        onChange={handleChange}
                        error={errors.count}
                        helperText={errors.count && "Обязательное поле (больше 0)"}
                        required
                        fullWidth
                    />
                    <TextField
                        label="ID товара"
                        name="product_id"
                        value={newOrder.product_id}
                        onChange={handleChange}
                        error={errors.product_id}
                        helperText={errors.product_id && "Обязательное поле (больше 0)"}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Цена"
                        name="price"
                        value={newOrder.price}
                        onChange={handleChange}
                        error={errors.product_id}
                        helperText={errors.product_id && "Обязательное поле (больше 0)"}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Дата создания"
                        type="date"
                        name="createDate"
                        slotProps={{
                            inputLabel: {
                                shrink: true,
                            },
                        }}
                        value={newOrder.createDate}
                        onChange={handleChange}
                        fullWidth
                    />
                    <TextField
                        label="Дата завершения"
                        type="date"
                        name="deadline"
                        slotProps={{
                            inputLabel: {
                                shrink: true,
                            },
                        }}
                        value={newOrder.deadline}
                        onChange={handleChange}
                        error={errors.deadline}
                        helperText={errors.deadline && "Должен быть позже даты заказа"}
                        fullWidth
                    />
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Отмена</Button>
                <Button onClick={handleSubmit} variant="contained" color="primary">
                    Создать
                </Button>
            </DialogActions>
        </Dialog>
    )
}
export {OrderCreate}
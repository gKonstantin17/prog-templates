import { DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog,
} from '@mui/material';
import {OrderApi} from "./OrderApi";
import {useState} from "react";
import Box from "@mui/material/Box";
function OrderUpdate({open,onClose,reload, id}) {
    // окно изменения заказа, использует OrderApi
    const {update} = OrderApi();

    // updateOrder - объект с полями для изменения заказа
    const [updateOrder, setUpdateOrder] = useState({
        createDate: null,
        deadline: null,
        count: null,
        product_id: null,
        price:null
    });
    // при изменении поля ввода, сохранять в updateOrder
    const handleChange = (e) => {
        const {name,value} = e.target;
        setUpdateOrder(prev => ({
            ...prev,
            [name]:value
        }))

    }

    // По нажатию изменить
    const handleSubmit = async () => {
        if (!validate()) return;            // проверить данные
        try {
            await update(updateOrder,id);   // изменить
            onClose();                      // закрыть окно
            setUpdateOrder({          // очистить поля
                createDate: null,
                deadline: null,
                count: null,
                product_id: null,
                price:null
            });
            reload()                        // обновить таблицу со всеми заказами
        } catch (e) {
            console.log(e)
        }
    }

    // есть ли ошибки при проверке полей
    const [errors, setErrors] = useState({
        count: false,
        product_id: false,
        price: false,
        createDate: false,
        deadline: false
    });

    // ошибка всей формы (когда ничего не было введено)
    const [formError,setFormError] = useState('');

    // проверка полей
    const validate = () => {
        // проверка заполнено ли хотя бы одно поле
        const isAnyFieldFilled = Object.values(updateOrder).some(
            value => value !== null && value !== ''
        );
        if (!isAnyFieldFilled) {
            setFormError('Заполните хотя бы одно поле');
            return false;
        }

        // проверка заполненных полей
        const newErrors = {
            count: updateOrder.count !== null && updateOrder.count < 0,
            product_id: updateOrder.product_id !== null && updateOrder.product_id < 0,
            price: updateOrder.price !== null && updateOrder.price < 0,
            deadline: (
                updateOrder.createDate &&
                updateOrder.deadline &&
                new Date(updateOrder.deadline) < new Date(updateOrder.createDate)
            )
        };
        setErrors(newErrors);
        return !Object.values(newErrors).some(Boolean);
    };

    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Изменить заказ</DialogTitle>
            <DialogContent>
                <Box component="form"
                     noValidate
                     autoComplete="off"
                     sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                    {formError && <div style={{ color: 'red' }}>{formError}</div>}
                    <TextField
                        label='Количество'
                        name='count'
                        value={updateOrder.count}
                        onChange={handleChange}
                        error={errors.count}
                        helperText={errors.count && "Должно быть больше 0"}
                        fullWidth
                    />
                    <TextField
                        label='ID товара'
                        name='product_id'
                        value={updateOrder.product_id}
                        onChange={handleChange}
                        error={errors.product_id}
                        helperText={errors.product_id && "Должно быть больше 0"}
                        fullWidth
                    />
                    <TextField
                        label='Цена'
                        name='price'
                        value={updateOrder.price}
                        onChange={handleChange}
                        error={errors.price}
                        helperText={errors.price && "Должно быть больше 0"}
                        fullWidth
                    />
                    <TextField
                        label='Дата заказа'
                        name='createDate'
                        value={updateOrder.createDate}
                        onChange={handleChange}
                        fullWidth
                    />
                    <TextField
                        label='Дата завершнения'
                        name='deadline'
                        value={updateOrder.deadline}
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
                    Изменить
                </Button>
            </DialogActions>
        </Dialog>
    )
}
export {OrderUpdate}
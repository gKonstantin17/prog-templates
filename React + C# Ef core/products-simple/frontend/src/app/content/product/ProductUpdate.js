import {ProductApi} from "./ProductApi";
import {
    DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog, Box,
} from '@mui/material';
import {useState} from "react";

function ProductUpdate({open,onClose,reload, id}) {
    // окно изменения товара, использует ProductApi
    const {update} = ProductApi();

    // updateProduct - объект с полями для изменения заказа
    const [updateProduct,setUpdateProduct] = useState({
        name: null,
        count: null,
    })
    // при изменении поля ввода, сохранять в updateProduct
    const handleChange = (e) => {
        const {name,value} = e.target;
        setUpdateProduct(prev => ({
            ...prev,
            [name]:value
        }))

    }

    // По нажатию изменить
    const handleSubmit = async () => {
        if (!validate()) return;            // проверить данные
        try {
            await update(updateProduct,id); // изменить
            onClose();                      // закрыть окно
            setUpdateProduct({        // очистить поля
                name: null,
                count: null,
            });
            reload()                        // обновить таблицу со всеми товарами
        } catch (e) {
            console.log(e)
        }
    }
    // есть ли ошибки при проверке полей
    const [errors, setErrors] = useState({
        name: null,
        count: null,
    });
    // ошибка всей формы (когда ничего не было введено)
    const [formError,setFormError] = useState('');

    // проверка полей
    const validate = () => {
        // проверка заполнено ли хотя бы одно поле
        const isAnyFieldFilled = Object.values(updateProduct).some(
            value => value !== null && value !== ''
        );
        if (!isAnyFieldFilled) {
            setFormError('Заполните хотя бы одно поле');
            return false;
        }

        // проверка заполненных полей
        const newErrors = {
            count: updateProduct.count !== null || updateProduct.count < 0
        };
        setErrors(newErrors);
        return !Object.values(newErrors).some(Boolean);
    };
    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Изменить товар</DialogTitle>
            <DialogContent>
                <Box component="form"
                     noValidate
                     autoComplete="off"
                     sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                    {formError && <div style={{ color: 'red' }}>{formError}</div>}
                    <TextField
                        label='Название'
                        name='name'
                        value={updateProduct.name}
                        onChange={handleChange}
                        fullWidth
                    />
                    <TextField
                        label='Количество'
                        name='count'
                        value={updateProduct.count}
                        onChange={handleChange}
                        error={errors.count}
                        helperText={errors.count && "Должно быть больше 0"}
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
export {ProductUpdate}
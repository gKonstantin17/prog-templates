import {ProductApi} from "./ProductApi";
import {
    DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog, Box,
} from '@mui/material';
import {useState} from "react";

function ProductCreate({open,onClose, reload, level,parent_id}) {
    // окно создания товара, использует ProductApi
    const {create} = ProductApi()

    // newProduct - объект с полями для создания заказа
    const [newProduct,setNewProduct] = useState({
        name: null,
        count: null,
    })

    // при изменении поля ввода, сохранять в newProduct
    const handleChange = (e) => {
        const { name, value } = e.target;
        setNewProduct(prev => ({
            ...prev,
            [name]: value
        }));
        setErrors(prev => ({...prev, [name]: false}));
    };

    // по нажатию Создать
    const handleSubmit = async () => {
        if (!validate()) return; // проверить данные
        try {
            await create(newProduct,level,parent_id);   // создать
            onClose();                                  // закрыть окно
            setNewProduct({                       // очистить поля
                name: null,
                count: null,
            });
            reload()                                    // обновить таблицу со всеми товарами
        } catch (err) {
            console.error('Error creating order:', err);
        }
    };

    // есть ли ошибки при проверке полей
    const [errors, setErrors] = useState({
        name: false,
        count: false,
    });

    // проверка полей
    const validate = () => {
        const newErrors = {
            name: !newProduct.name,
            count: !newProduct.count || newProduct.count < 0
        };
        setErrors(newErrors);
        return !Object.values(newErrors).some(Boolean);
    };
    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Добавить новый товар</DialogTitle>
            <DialogContent>
                <Box component="form"
                     noValidate
                     autoComplete="off"
                     sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                    <TextField
                        label="Название"
                        name="name"
                        value={newProduct.name}
                        onChange={handleChange}
                        error={errors.name}
                        helperText={errors.name && "Обязательное поле"}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Количество"
                        name="count"
                        value={newProduct.count}
                        onChange={handleChange}
                        error={errors.count}
                        helperText={errors.count && "Обязательное поле (больше 0)"}
                        required
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
export {ProductCreate}
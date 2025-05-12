import {WarehouseApi} from "./WarehouseApi";
import { DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog,
} from '@mui/material';
import {useEffect, useState} from "react";
import Box from "@mui/material/Box";
function WarehouseChange({open,onClose,reload,product}) {
    const {change} = WarehouseApi();

    const [changeWh,setChangeWh] = useState({
        number: null,
        productId: product || null,
    })
    useEffect(() => {
        if (open) {
            setChangeWh({
                number: null,
                productId: product || null,
            });
            setErrors({
                number: null,
                productId: null,
            });
        }
    }, [open, product]);
    const handleChange = (e) => {
        const {name,value} = e.target;
        setChangeWh(prev => ({
            ...prev,
            [name]:value
        }))

    }
    const handleSubmit = async () => {
        if (!validate()) return;
        try {
            await change(changeWh);
            onClose();
            setChangeWh({
                number: null,
                productId: null,
            });
            reload()
        } catch (e) {
            console.log(e)
        }
    }

    const [errors, setErrors] = useState({
        number: null,
        productId: null,
    });
    const validate = () => {
        const newErrors = {
            number: !changeWh.number,
            productId: !changeWh.productId || changeWh.productId < 0,
        };
        setErrors(newErrors);
        return !Object.values(newErrors).some(Boolean);
    };
    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Изменить товар на складе</DialogTitle>
            <DialogContent>
                <Box component="form"
                     noValidate
                     autoComplete="off"
                     sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>

                    <TextField
                        label="Количество ±"
                        name="number"
                        value={changeWh.number}
                        onChange={handleChange}
                        error={errors.number}
                        helperText={errors.number && "Обязательное поле"}
                        required
                        fullWidth
                    />
                    <TextField
                        label="ID товара"
                        name="productId"
                        value={changeWh.productId}
                        onChange={handleChange}
                        error={errors.productId}
                        helperText={errors.productId && "Обязательное поле (больше 0)"}
                        required
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
export {WarehouseChange}
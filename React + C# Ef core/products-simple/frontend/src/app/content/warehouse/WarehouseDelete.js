import {WarehouseApi} from "./WarehouseApi";
import { DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog,
} from '@mui/material';
import {useEffect, useState} from "react";
import Box from "@mui/material/Box";
function WarehouseDelete({open,onClose,reload, id}) {
    const {deleteFromHistory} = WarehouseApi();
    const [deleteWh,setDeleteWh] = useState({
        id: id || null
    })
    useEffect(() => {
        if (open) {
            setDeleteWh({
                id: id || null
            });
        }
    }, [open, id]);
    const handleChange = (e) => {
        const {name,value} = e.target;
        setDeleteWh(prev => ({
            ...prev,
            [name]:value
        }))

    }
    const handleSubmit = async () => {
        try {
            await deleteFromHistory(deleteWh.id);
            onClose();
            setDeleteWh({
                id: null
            });
            reload()
        } catch (e) {
            console.log(e)
        }
    }
    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Удаление</DialogTitle>
            <DialogContent>
                <Box sx={{gap:2,mt:2}}>
                    <TextField
                        label="ID записи из истории"
                        name="id"
                        value={deleteWh.id}
                        onChange={handleChange}
                        required
                        fullWidth
                    />
                    Вы уверены что хотите удалить?
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} >Отмена</Button>
                <Button onClick={handleSubmit}>Удалить</Button>
            </DialogActions>
        </Dialog>
    )
}
export {WarehouseDelete}
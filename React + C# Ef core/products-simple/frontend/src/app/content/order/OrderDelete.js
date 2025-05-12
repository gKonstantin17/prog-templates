import {OrderApi} from "./OrderApi";
import {
    DialogTitle, DialogContent, DialogActions, Button, Dialog, Box,
} from '@mui/material';
function OrderDelete({open,onClose,reload, id}) {
    // окно удаления заказа, использует OrderApi
    const {deleteOrder} = OrderApi()

    // по нажатию Удалить
    const handleSubmit = async () => {
        await deleteOrder(id)   // удалить
        onClose()               // закрыть окно
        reload()                // обновить таблицу со всеми заказами
    }
    return(
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Удаление</DialogTitle>
            <DialogContent>
                <Box sx={{gap:2,mt:2}}>
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
export {OrderDelete}
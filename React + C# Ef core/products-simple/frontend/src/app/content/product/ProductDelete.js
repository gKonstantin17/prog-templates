import {ProductApi} from "./ProductApi";
import {
    DialogTitle, DialogContent, DialogActions, TextField, Button, Dialog, Box,
} from '@mui/material';
function ProductDelete({open,onClose,reload, id}) {
    // окно удаления товара, использует ProductApi
    const {deleteProduct} = ProductApi()
    // по нажатию Удалить
    const handleSubmit = async () => {
        await deleteProduct(id) // удалить
        onClose()               // закрыть окно
        reload()                // обновить таблицу со всеми товарами
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
export {ProductDelete}
import {WarehouseApi} from "./WarehouseApi";
import {
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Dialog,
    TableContainer,
    Paper,
    Table,
    TableHead,
    TableRow, TableCell, TableBody,
} from '@mui/material';
import {useEffect, useState} from "react";
import Box from "@mui/material/Box";
function WarehouseHistory({open,onClose,product}) {
    const {historyChanges} = WarehouseApi();
    const [history, setHistory] = useState([]);

    useEffect(() => {
        if (product && open) {
            historyChanges(product).then(setHistory);
        }
    }, [product, open]);

    return(<div>
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>История изменений</DialogTitle>
            <DialogContent>
                <Box component="form"
                     noValidate
                     autoComplete="off"
                     sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>

                    <TableContainer component={Paper} sx={{ maxHeight: 400, overflowY: 'auto' }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Id</TableCell>
                                    <TableCell>Id товара</TableCell>
                                    <TableCell>Количество</TableCell>
                                    <TableCell>Дата изменения</TableCell>
                                </TableRow>
                            </TableHead>
                            {history && <TableBody>
                                {history.map((whProduct, idx) => (
                                    <TableRow key={idx}>
                                        <TableCell>{whProduct.id}</TableCell>
                                        <TableCell>{whProduct.product_Id}</TableCell>
                                        <TableCell>{whProduct.count}</TableCell>
                                        <TableCell>{new Date(whProduct.updateDate).toLocaleString()}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>}
                        </Table>
                    </TableContainer>

                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Закрыть</Button>
            </DialogActions>
        </Dialog>

    </div>)
}
export {WarehouseHistory}
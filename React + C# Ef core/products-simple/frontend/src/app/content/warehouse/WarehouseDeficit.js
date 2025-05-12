import { WarehouseApi } from "./WarehouseApi";
import { DialogTitle, DialogContent, DialogActions, Button, Dialog, TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody } from '@mui/material';
import { useEffect, useState } from "react";
import Box from "@mui/material/Box";

function WarehouseDeficit({ open, onClose }) {
    const { checkComponents } = WarehouseApi();
    const [deficitData, setDeficitData] = useState([]);

    useEffect(() => {
        if (open) {
            checkComponents().then(setDeficitData);
        }
    }, [open]);



    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Дефицит товаров на складе</DialogTitle>
            <DialogContent>
                <Box component="form" noValidate autoComplete="off" sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                    <TableContainer component={Paper} sx={{ maxHeight: 400, overflowY: 'auto' }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Id товара</TableCell>
                                    <TableCell>Необходимо</TableCell>
                                    <TableCell>В наличии</TableCell>
                                    <TableCell>Не хватает</TableCell>
                                </TableRow>
                            </TableHead>
                            {deficitData && (
                                <TableBody>
                                    {deficitData.map((item, idx) => (
                                        <TableRow key={idx}>
                                            <TableCell>{item.productId}</TableCell>
                                            <TableCell>{item.whDeficit.required}</TableCell>
                                            <TableCell>{item.whDeficit.available}</TableCell>
                                            <TableCell>{item.whDeficit.missing}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            )}
                        </Table>
                    </TableContainer>
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Закрыть</Button>
            </DialogActions>
        </Dialog>
    );
}

export { WarehouseDeficit };

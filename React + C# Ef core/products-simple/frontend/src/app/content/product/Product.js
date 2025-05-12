import {useEffect, useState} from 'react';
import {
    Box,
    Paper,
    List,
    ListItem,
    ListItemText,
    Collapse,
    IconButton,
    Typography
} from '@mui/material';
import {ProductApi} from "./ProductApi";
import {ProductCreate} from "./ProductCreate";
import {ProductUpdate} from "./ProductUpdate";
import {ProductDelete} from "./ProductDelete";



function Product() {
    // основной компонент, отображает список товаров в виде дерева
    // также с этой страницы запускаются окна для создания, изменения, удаления товаров
    // все запросы хранятся в ProductApi.js

    // для get запроса
    const {data, error,loading,getAll} = ProductApi();
    useEffect(() => { // выполнение запроса при отображении Product
        getAll()
    },[]);


    const [selectedProductId, setSelectedProductId] = useState(null); // выбранный id
    const [selectedLevel, setSelectedLevel] = useState(null); // выбранный level
    const [parentId, setParentId] = useState(null); // parent_id
    const [createModalOpen, setCreateModalOpen] = useState(false); // открыть создание
    const [updateModalOpen, setUpdateModalOpen] = useState(false); // открыть изменение
    const [deleteModalOpen, setDeleteModalOpen] = useState(false); // открыть удаление

    if (loading) return (<div>Загрузка данных...</div>)
    if (error) return (<div>{error.message}</div>)
    return (<div>
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button onClick={() => {
                    setSelectedLevel(0);
                    setParentId(null);
                    setCreateModalOpen(true);
                }}>➕</button>

            </div>
            {/*Модальные окна*/}
            <ProductCreate
            open={createModalOpen}
            onClose={() => setCreateModalOpen(false)}
            reload={getAll}
            level={selectedLevel}
            parent_id={parentId}
            />
            <ProductUpdate
            open={updateModalOpen}
            onClose={() => setUpdateModalOpen(false)}
            reload={getAll}
            id={selectedProductId}
            />
            <ProductDelete
            open={deleteModalOpen}
            onClose={() => setDeleteModalOpen(false)}
            reload={getAll}
            id={selectedProductId}
            />

            <Paper elevation={0} sx={{
                maxWidth: 800,
                margin: 'auto',
                border: '1px solid #e0e0e0',
                borderRadius: 2
            }}>

                {/* Список товаров в виде дерева */}
                <List>
                    {data.map(item => (
                        <TreeItem key={item.id} node={item}
                                  setSelectedLevel={setSelectedLevel}
                                  setParentId={setParentId}
                                  setCreateModalOpen={setCreateModalOpen}
                                  setSelectedProductId={setSelectedProductId}
                                  setUpdateModalOpen={setUpdateModalOpen}
                                  setDeleteModalOpen={setDeleteModalOpen}
                                  />
                    ))}
                </List>
            </Paper>
    </div>

    );
};

// вспомогательный компонент для отображения дерева
// получает функции из основного
const TreeItem = ({ node, level = 0,
                      setSelectedLevel,
                      setCreateModalOpen,
                      setParentId,
                      setSelectedProductId,
                      setUpdateModalOpen,
                      setDeleteModalOpen

                  }) => {
    const [open, setOpen] = useState(false);
    const hasChildren = node.specifications && node.specifications.length > 0;
    const paddingLeft = level * 2 + 'rem';

    return (
        <>
            <ListItem
                sx={{
                    pl: paddingLeft,
                    borderBottom: '1px solid rgba(0,0,0,0.08)',
                    backgroundColor: level === 0 ? '#f9f9f9' : 'inherit'
                }}
            >
                {hasChildren && (
                    <IconButton
                        size="small"
                        onClick={() => setOpen(!open)}
                        sx={{ mr: 1 }}
                    >
                        {open ? '▼' : '►'}
                    </IconButton>
                )}
                {!hasChildren && <Box width={32} sx={{ mr: 1 }} />} {/* Отступ для выравнивания */}

                <ListItemText
                    primary={
                        <Typography variant={level === 0 ? 'subtitle1' : 'body1'}>
                            {node.name}
                        </Typography>
                    }
                    secondary={`ID: ${node.id}, Количество: ${node.count}`}
                    sx={{ flexGrow: 1 }}
                />

                <Box>
                    <IconButton size="small" sx={{ color: 'text.secondary' }}
                                onClick={() => {
                                    setSelectedLevel(node.level+1);
                                    setParentId(node.id);
                                    setCreateModalOpen(true);
                                }}>
                        ➕
                    </IconButton>
                    <IconButton size="small" sx={{ color: 'text.secondary' }}
                                onClick={() => {
                                    setSelectedProductId(node.id);
                                    setUpdateModalOpen(true);
                                }}>
                        ✏️
                    </IconButton>
                    <IconButton size="small" sx={{ color: 'text.secondary' }}
                                onClick={() => {
                                    setSelectedProductId(node.id);
                                    setDeleteModalOpen(true);
                                }}>🗑️</IconButton>
                </Box>
            </ListItem>

            {hasChildren && (
                <Collapse in={open} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding>
                        {node.specifications.map(child => (
                            <TreeItem key={child.id} node={child} level={level + 1}
                                      setSelectedLevel={setSelectedLevel}
                                      setParentId={setParentId}
                                      setCreateModalOpen={setCreateModalOpen}
                                      setSelectedProductId={setSelectedProductId}
                                      setUpdateModalOpen={setUpdateModalOpen}
                                      setDeleteModalOpen={setDeleteModalOpen}
                            />
                        ))}
                    </List>
                </Collapse>
            )}
        </>
    );
};

export {Product}

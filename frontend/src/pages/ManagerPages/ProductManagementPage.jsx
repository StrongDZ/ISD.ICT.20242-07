import React, { useState, useEffect } from "react";
import {
    Container,
    Typography,
    Box,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    Chip,
    Alert,
    Snackbar,
    Tooltip,
    Pagination,
    Card,
    CardContent,
    InputAdornment,
} from "@mui/material";
import { Add, Edit, Delete, Visibility, Search, FilterList, Save, Cancel, Warning } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import ProductEditDialog from "../../components/Product/ProductEditDialog";
import ProductDetailDialog from "../../components/Product/ProductDetailDialog";

const ProductManagementPage = () => {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [categoryFilter, setCategoryFilter] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);

    // Dialog states
    const [editDialog, setEditDialog] = useState({ open: false, product: null, mode: "view" });
    const [detailDialog, setDetailDialog] = useState({ open: false, product: null });
    const [deleteDialog, setDeleteDialog] = useState({ open: false, product: null });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        loadProducts();
    }, []);

    useEffect(() => {
        filterProducts();
    }, [products, searchQuery, categoryFilter]);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const mockProducts = productService.getMockProducts();
            setProducts(mockProducts);
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to load products",
                severity: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    const filterProducts = () => {
        let filtered = products;

        if (searchQuery) {
            filtered = filtered.filter(
                (product) =>
                    product.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    product.productID.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }

        if (categoryFilter) {
            filtered = filtered.filter((product) => product.category === categoryFilter);
        }

        setFilteredProducts(filtered);
        setCurrentPage(1);
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const getCategoryColor = (category) => {
        switch (category?.toLowerCase()) {
            case "book":
                return "primary";
            case "cd":
                return "warning";
            case "dvd":
                return "secondary";
            case "lp":
                return "success";
            default:
                return "default";
        }
    };

    const getStockStatus = (quantity) => {
        if (quantity === 0) return { color: "error", label: "Out of Stock" };
        if (quantity <= 5) return { color: "warning", label: "Low Stock" };
        return { color: "success", label: "In Stock" };
    };

    const handleView = (product) => {
        setDetailDialog({ open: true, product });
    };

    const handleEdit = (product) => {
        setEditDialog({ open: true, product: { ...product }, mode: "edit" });
    };

    const handleAdd = () => {
        setEditDialog({ open: true, product: null, mode: "add" });
    };

    const handleSave = async (productData, mode) => {
        try {
            // Validation
            if (!productData.title || !productData.price || productData.quantity === undefined) {
                setSnackbar({
                    open: true,
                    message: "Please fill in all required fields",
                    severity: "error",
                });
                return;
            }

            if (mode === "add") {
                // In real app, this would call the API
                console.log("Adding product:", productData);
                setSnackbar({
                    open: true,
                    message: "Product added successfully",
                    severity: "success",
                });
            } else {
                // In real app, this would call the API
                console.log("Updating product:", productData);
                setSnackbar({
                    open: true,
                    message: "Product updated successfully",
                    severity: "success",
                });
            }

            setEditDialog({ open: false, product: null, mode: "view" });
            // In real app, reload products from API
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to save product",
                severity: "error",
            });
        }
    };

    const handleDelete = async () => {
        try {
            const { product } = deleteDialog;
            console.log("Deleting product:", product.productID);

            setSnackbar({
                open: true,
                message: "Product deleted successfully",
                severity: "success",
            });

            setDeleteDialog({ open: false, product: null });
            // In real app, reload products from API
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to delete product",
                severity: "error",
            });
        }
    };

    const handleEditFromDetail = (product) => {
        setDetailDialog({ open: false, product: null });
        setEditDialog({ open: true, product: { ...product }, mode: "edit" });
    };

    // Pagination
    const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginatedProducts = filteredProducts.slice(startIndex, startIndex + itemsPerPage);

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Header */}
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Product Management
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Manage your product inventory, update details, and add new products.
                </Typography>
            </Box>

            {/* Controls */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={4}>
                            <TextField
                                fullWidth
                                placeholder="Search products..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                InputProps={{
                                    startAdornment: (
                                        <InputAdornment position="start">
                                            <Search />
                                        </InputAdornment>
                                    ),
                                }}
                            />
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <FormControl fullWidth>
                                <InputLabel>Category</InputLabel>
                                <Select
                                    value={categoryFilter}
                                    onChange={(e) => setCategoryFilter(e.target.value)}
                                    label="Category"
                                    startAdornment={<FilterList />}
                                >
                                    <MenuItem value="">All Categories</MenuItem>
                                    <MenuItem value="book">Books</MenuItem>
                                    <MenuItem value="cd">CDs</MenuItem>
                                    <MenuItem value="dvd">DVDs</MenuItem>
                                    <MenuItem value="lp">LPs (Vinyl)</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <Typography variant="body2" color="text.secondary">
                                {filteredProducts.length} products found
                            </Typography>
                        </Grid>
                        <Grid item xs={12} sm={2}>
                            <Button variant="contained" startIcon={<Add />} onClick={handleAdd} fullWidth>
                                Add Product
                            </Button>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            {/* Products Table */}
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Product</TableCell>
                            <TableCell>Category</TableCell>
                            <TableCell>Price</TableCell>
                            <TableCell>Stock</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedProducts.map((product) => {
                            const stockStatus = getStockStatus(product.quantity);
                            return (
                                <TableRow key={product.productID} hover>
                                    <TableCell>
                                        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                                            <img
                                                src={product.imageURL}
                                                alt={product.title}
                                                style={{
                                                    width: 50,
                                                    height: 50,
                                                    objectFit: "cover",
                                                    borderRadius: 4,
                                                }}
                                            />
                                            <Box>
                                                <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                    {product.title.length > 40 ? product.title.substring(0, 40) + "..." : product.title}
                                                </Typography>
                                                <Typography variant="caption" color="text.secondary">
                                                    ID: {product.productID}
                                                </Typography>
                                            </Box>
                                        </Box>
                                    </TableCell>
                                    <TableCell>
                                        <Chip label={product.category.toUpperCase()} color={getCategoryColor(product.category)} size="small" />
                                    </TableCell>
                                    <TableCell>{formatPrice(product.price)}</TableCell>
                                    <TableCell>
                                        <Typography color={stockStatus.color} sx={{ fontWeight: "bold" }}>
                                            {product.quantity}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Chip
                                            label={stockStatus.label}
                                            color={stockStatus.color}
                                            size="small"
                                            icon={product.quantity <= 5 ? <Warning /> : undefined}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <Box sx={{ display: "flex", gap: 1 }}>
                                            <Tooltip title="View Details">
                                                <IconButton size="small" onClick={() => handleView(product)}>
                                                    <Visibility />
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title="Edit Product">
                                                <IconButton size="small" onClick={() => handleEdit(product)}>
                                                    <Edit />
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title="Delete Product">
                                                <IconButton size="small" color="error" onClick={() => setDeleteDialog({ open: true, product })}>
                                                    <Delete />
                                                </IconButton>
                                            </Tooltip>
                                        </Box>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Pagination */}
            {totalPages > 1 && (
                <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                    <Pagination count={totalPages} page={currentPage} onChange={(e, page) => setCurrentPage(page)} color="primary" />
                </Box>
            )}

            {/* Edit/Add Dialog */}
            <ProductEditDialog
                open={editDialog.open}
                onClose={() => setEditDialog({ open: false, product: null, mode: "view" })}
                productData={editDialog.product}
                mode={editDialog.mode}
                onSave={handleSave}
            />

            {/* Detail Dialog */}
            <ProductDetailDialog
                open={detailDialog.open}
                onClose={() => setDetailDialog({ open: false, product: null })}
                product={detailDialog.product}
                onEdit={handleEditFromDetail}
            />

            {/* Delete Confirmation Dialog */}
            <Dialog open={deleteDialog.open} onClose={() => setDeleteDialog({ open: false, product: null })}>
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to delete "{deleteDialog.product?.title}"? This action cannot be undone.</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialog({ open: false, product: null })}>Cancel</Button>
                    <Button onClick={handleDelete} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Snackbar */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={3000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            >
                <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default ProductManagementPage;

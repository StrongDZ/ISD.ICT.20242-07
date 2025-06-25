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
    CircularProgress,
    Stack,
} from "@mui/material";
import { Add, Edit, Delete, Visibility, Search, FilterList, Save, Cancel, Warning, Clear } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import ProductEditDialog from "../../components/Product/ProductEditDialog";
import ProductDetailDialog from "../../components/Product/ProductDetailDialog";

const ProductManagementPage = () => {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Pagination state
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Dialog states
    const [editDialog, setEditDialog] = useState({ open: false, product: null, mode: "view" });
    const [detailDialog, setDetailDialog] = useState({ open: false, product: null });
    const [deleteDialog, setDeleteDialog] = useState({ open: false, product: null });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    // Filter states
    const [filters, setFilters] = useState({
        search: "",
        category: "",
        stockStatus: "",
    });

    const categories = [
        { value: "", label: "All Categories" },
        { value: "book", label: "Books" },
        { value: "cd", label: "CDs" },
        { value: "dvd", label: "DVDs" },
    ];

    const stockStatuses = [
        { value: "", label: "All Stock Levels" },
        { value: "in_stock", label: "In Stock" },
        { value: "low_stock", label: "Low Stock (≤5)" },
        { value: "out_of_stock", label: "Out of Stock" },
    ];

    useEffect(() => {
        loadProducts();
    }, [currentPage, filters]);

    const loadProducts = async () => {
        try {
            setLoading(true);
            setError(null);

            let response;
            const backendPage = currentPage - 1; // Convert to 0-based pagination for backend

            if (filters.search) {
                response = await productService.searchProducts(filters.search, backendPage, pageSize);
            } else if (filters.category) {
                response = await productService.getProductsByCategory(filters.category, backendPage, pageSize);
            } else {
                response = await productService.getAllProducts(backendPage, pageSize);
            }

            let productList = [];
            if (response.content) {
                // Paginated response
                productList = response.content;
                setTotalPages(response.totalPages);
                setTotalElements(response.totalElements);
            } else if (Array.isArray(response)) {
                // Non-paginated response - apply client-side pagination
                productList = response;
                setTotalPages(Math.ceil(response.length / pageSize));
                setTotalElements(response.length);
            }

            setProducts(productList);
            filterProducts(productList);
        } catch (err) {
            console.error("Error loading products:", err);
            setError(err.message);

            // Fallback to mock data
            try {
                const mockProducts = productService.getMockProducts();
                setProducts(mockProducts);
                setTotalPages(Math.ceil(mockProducts.length / pageSize));
                setTotalElements(mockProducts.length);
                filterProducts(mockProducts);
            } catch (mockError) {
                setProducts([]);
            }
        } finally {
            setLoading(false);
        }
    };

    const filterProducts = (productList = products) => {
        let filtered = [...productList];

        // Apply search filter
        if (filters.search) {
            const searchTerm = filters.search.toLowerCase();
            filtered = filtered.filter(
                (product) =>
                    product.title.toLowerCase().includes(searchTerm) ||
                    product.description.toLowerCase().includes(searchTerm) ||
                    (product.authors && product.authors.toLowerCase().includes(searchTerm)) ||
                    (product.artist && product.artist.toLowerCase().includes(searchTerm)) ||
                    (product.director && product.director.toLowerCase().includes(searchTerm))
            );
        }

        // Apply category filter (for client-side filtering when using non-paginated API)
        if (filters.category) {
            filtered = filtered.filter((product) => product.category === filters.category);
        }

        // Apply stock status filter
        if (filters.stockStatus) {
            switch (filters.stockStatus) {
                case "in_stock":
                    filtered = filtered.filter((product) => product.quantity > 5);
                    break;
                case "low_stock":
                    filtered = filtered.filter((product) => product.quantity > 0 && product.quantity <= 5);
                    break;
                case "out_of_stock":
                    filtered = filtered.filter((product) => product.quantity === 0);
                    break;
                default:
                    break;
            }
        }

        setFilteredProducts(filtered);
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
            default:
                return "default";
        }
    };

    const getStockStatus = (quantity) => {
        if (quantity === 0) return { label: "Out of Stock", color: "error" };
        if (quantity <= 5) return { label: "Low Stock", color: "warning" };
        return { label: "In Stock", color: "success" };
    };

    const handleView = (product) => {
        setDetailDialog({ open: true, product });
    };

    const handleEdit = (product) => {
        setEditDialog({ open: true, product, mode: "edit" });
    };

    const handleAdd = () => {
        setEditDialog({ open: true, product: null, mode: "add" });
    };

    const handleSave = async (productData, mode) => {
        try {
            if (mode === "add") {
                await productService.createProduct(productData);
            } else {
                await productService.updateProduct(productData.productID, productData);
            }

            setEditDialog({ open: false, product: null, mode: "view" });
            loadProducts(); // Reload products after save
        } catch (error) {
            console.error("Error saving product:", error);
            throw new Error(error.response?.data?.message || "Failed to save product");
        }
    };

    const handleDelete = async () => {
        try {
            await productService.deleteProduct(deleteDialog.product.productID);
            setDeleteDialog({ open: false, product: null });
            loadProducts(); // Reload products after delete
        } catch (error) {
            console.error("Error deleting product:", error);
            setError("Failed to delete product");
        }
    };

    const handleEditFromDetail = (product) => {
        setDetailDialog({ open: false, product: null });
        setEditDialog({ open: true, product, mode: "edit" });
    };

    const handleFilterChange = (field, value) => {
        const newFilters = { ...filters, [field]: value };
        setFilters(newFilters);
        setCurrentPage(1); // Reset to first page when filters change
    };

    const handlePageChange = (event, page) => {
        setCurrentPage(page);
    };

    const handleClearFilters = () => {
        setFilters({
            search: "",
            category: "",
            stockStatus: "",
        });
        setCurrentPage(1);
    };

    return (
        <Container maxWidth="xl" sx={{ py: 4 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
                <Typography variant="h4" component="h1" fontWeight="bold">
                    Product Management
                </Typography>
                <Button variant="contained" startIcon={<Add />} onClick={handleAdd}>
                    Add Product
                </Button>
            </Box>

            {/* Statistics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 3, textAlign: "center" }}>
                        <Typography variant="h4" color="primary" fontWeight="bold">
                            {totalElements}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Total Products
                        </Typography>
                    </Paper>
                </Grid>
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 3, textAlign: "center" }}>
                        <Typography variant="h4" color="success.main" fontWeight="bold">
                            {products.filter((p) => p.quantity > 5).length}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            In Stock
                        </Typography>
                    </Paper>
                </Grid>
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 3, textAlign: "center" }}>
                        <Typography variant="h4" color="warning.main" fontWeight="bold">
                            {products.filter((p) => p.quantity > 0 && p.quantity <= 5).length}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Low Stock
                        </Typography>
                    </Paper>
                </Grid>
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 3, textAlign: "center" }}>
                        <Typography variant="h4" color="error.main" fontWeight="bold">
                            {products.filter((p) => p.quantity === 0).length}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Out of Stock
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>

            {/* Filters */}
            <Paper sx={{ p: 3, mb: 4 }}>
                <Grid container spacing={3} alignItems="center">
                    <Grid item xs={12} md={4}>
                        <TextField
                            fullWidth
                            size="small"
                            placeholder="Search products..."
                            value={filters.search}
                            onChange={(e) => handleFilterChange("search", e.target.value)}
                            InputProps={{
                                startAdornment: <Search sx={{ mr: 1, color: "text.secondary" }} />,
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={6} md={2}>
                        <FormControl fullWidth size="small">
                            <InputLabel>Category</InputLabel>
                            <Select value={filters.category} label="Category" onChange={(e) => handleFilterChange("category", e.target.value)}>
                                {categories.map((category) => (
                                    <MenuItem key={category.value} value={category.value}>
                                        {category.label}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={6} md={2}>
                        <FormControl fullWidth size="small">
                            <InputLabel>Stock Status</InputLabel>
                            <Select
                                value={filters.stockStatus}
                                label="Stock Status"
                                onChange={(e) => handleFilterChange("stockStatus", e.target.value)}
                            >
                                {stockStatuses.map((status) => (
                                    <MenuItem key={status.value} value={status.value}>
                                        {status.label}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Button fullWidth variant="outlined" size="small" startIcon={<Clear />} onClick={handleClearFilters}>
                            Clear Filters
                        </Button>
                    </Grid>
                </Grid>
            </Paper>

            {/* Error Display */}
            {error && (
                <Alert severity="error" sx={{ mb: 4 }}>
                    {error}
                </Alert>
            )}

            {/* Loading State */}
            {loading && (
                <Box sx={{ display: "flex", justifyContent: "center", py: 8 }}>
                    <CircularProgress />
                </Box>
            )}

            {/* Products Table */}
            {!loading && (
                <>
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Image</TableCell>
                                    <TableCell>Title</TableCell>
                                    <TableCell>Category</TableCell>
                                    <TableCell>Price</TableCell>
                                    <TableCell>Stock</TableCell>
                                    <TableCell>Status</TableCell>
                                    <TableCell align="center">Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {filteredProducts.map((product) => (
                                    <TableRow key={product.productID} hover>
                                        <TableCell>
                                            <Box
                                                component="img"
                                                src={product.imageURL}
                                                alt={product.title}
                                                sx={{ width: 60, height: 60, objectFit: "cover", borderRadius: 1 }}
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <Typography variant="body1" fontWeight="medium">
                                                {product.title}
                                            </Typography>
                                            <Typography variant="caption" color="text.secondary">
                                                ID: {product.productID}
                                            </Typography>
                                        </TableCell>
                                        <TableCell>
                                            <Chip label={product.category?.toUpperCase()} color={getCategoryColor(product.category)} size="small" />
                                        </TableCell>
                                        <TableCell>{formatPrice(product.price)}</TableCell>
                                        <TableCell>{product.quantity}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={getStockStatus(product.quantity).label}
                                                color={getStockStatus(product.quantity).color}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell align="center">
                                            <IconButton size="small" onClick={() => handleView(product)} color="info">
                                                <Visibility />
                                            </IconButton>
                                            <IconButton size="small" onClick={() => handleEdit(product)} color="primary">
                                                <Edit />
                                            </IconButton>
                                            <IconButton size="small" onClick={() => setDeleteDialog({ open: true, product })} color="error">
                                                <Delete />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
                            <Stack spacing={2}>
                                <Pagination
                                    count={totalPages}
                                    page={currentPage}
                                    onChange={handlePageChange}
                                    color="primary"
                                    size="large"
                                    showFirstButton
                                    showLastButton
                                />
                                <Typography variant="body2" color="text.secondary" align="center">
                                    Page {currentPage} of {totalPages} ({totalElements} total products)
                                </Typography>
                            </Stack>
                        </Box>
                    )}
                </>
            )}

            {/* Dialogs */}
            <ProductEditDialog
                open={editDialog.open}
                onClose={() => setEditDialog({ open: false, product: null, mode: "view" })}
                productData={editDialog.product}
                mode={editDialog.mode}
                onSave={handleSave}
            />

            <ProductDetailDialog
                open={detailDialog.open}
                onClose={() => setDetailDialog({ open: false, product: null })}
                product={detailDialog.product}
                onEdit={handleEditFromDetail}
            />

            {/* Delete Confirmation Dialog */}
            {deleteDialog.open && (
                <Dialog open={deleteDialog.open} onClose={() => setDeleteDialog({ open: false, product: null })}>
                    <DialogTitle>Delete Product</DialogTitle>
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
            )}

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

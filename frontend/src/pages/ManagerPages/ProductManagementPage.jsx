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
    Checkbox,
    List,
    ListItem,
    ListItemText,
    ListItemAvatar,
    Avatar,
    LinearProgress,
    Slide,
    Divider,
} from "@mui/material";
import { Add, Edit, Delete, Visibility, Search, FilterList, Save, Cancel, Warning, Clear, SelectAll, ClearAll, CheckCircle } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { productService } from "../../services/productService";
import ProductEditDialog from "../../components/Product/ProductEditDialog";
import ProductDetailDialog from "../../components/Product/ProductDetailDialog";
import { getCategoryColor } from "../../utils/getCategoryColor";

const ProductManagementPage = () => {
    const navigate = useNavigate();
    const { user, isAuthenticated, isManager } = useAuth();
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Pagination state
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(20);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Dialog states
    const [editDialog, setEditDialog] = useState({ open: false, product: null, mode: "view" });
    const [detailDialog, setDetailDialog] = useState({ open: false, product: null });
    const [deleteDialog, setDeleteDialog] = useState({ open: false, product: null });
    const [bulkDeleteDialog, setBulkDeleteDialog] = useState({ open: false, products: [] });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
    
    // Bulk operations states
    const [isDeleting, setIsDeleting] = useState(false);
    const [deleteProgress, setDeleteProgress] = useState(0);

    // Filter states
    const [filters, setFilters] = useState({
        search: "",
        category: "",
        stockStatus: "",
    });

    const [selectedProducts, setSelectedProducts] = useState([]);

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

            const params = {};
            if (filters.search) {
                params.keyword = filters.search;
            }
            if (filters.category) {
                params.category = filters.category;
            }
            
            try {
                // Try paginated API first
                response = await productService.fetchProducts(params, backendPage, pageSize);
            } catch (paginatedError) {
                console.warn("Paginated API failed, falling back to non-paginated:", paginatedError);
                // Fallback to non-paginated API like Dashboard
                response = await productService.getAllProductsNoPagination();
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
            setError(`Failed to load products from database: ${err.message}`);
            setProducts([]);
            setTotalPages(0);
            setTotalElements(0);
            setFilteredProducts([]);
            
            setSnackbar({
                open: true,
                message: `❌ ${err.message}`,
                severity: "error"
            });
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
        // Check authentication status
        const token = localStorage.getItem("token");
        const storedUser = localStorage.getItem("user");
        
        console.log("🔍 Debug Authentication:", {
            isAuthenticated: isAuthenticated(),
            isManager: isManager(),
            user: user,
            storedUser: storedUser ? JSON.parse(storedUser) : null,
            token: token ? "Token exists" : "No token",
            tokenLength: token ? token.length : 0
        });

        if (!isAuthenticated()) {
            setSnackbar({
                open: true,
                message: "❌ Please login to add products",
                severity: "error"
            });
            navigate("/login");
            return;
        }

        if (!isManager()) {
            setSnackbar({
                open: true,
                message: "❌ Only managers can add products. Your role: " + (user?.role || "Unknown"),
                severity: "error"
            });
            return;
        }

        setEditDialog({ open: true, product: null, mode: "add" });
    };



    const handleSave = async (productData, mode) => {
        try {
            let result;
            if (mode === "add") {
                result = await productService.createProduct(productData);
                setSnackbar({ 
                    open: true, 
                    message: `✅ Product "${result.title || productData.title}" created successfully!`, 
                    severity: "success" 
                });
            } else {
                result = await productService.updateProduct(productData.productID, productData);
                setSnackbar({ 
                    open: true, 
                    message: `✅ Product "${result.title || productData.title}" updated successfully!`, 
                    severity: "success" 
                });
            }

            setEditDialog({ open: false, product: null, mode: "view" });
            await loadProducts(); // Reload products after save
        } catch (error) {
            console.error("Error saving product:", error);
            setSnackbar({ 
                open: true, 
                message: `❌ Failed to ${mode === "add" ? "create" : "update"} product: ${error.message}`, 
                severity: "error" 
            });
        }
    };

    const handleDelete = async () => {
        try {
            const productTitle = deleteDialog.product.title;
            await productService.deleteProduct(deleteDialog.product.productID);
            
            setSnackbar({ 
                open: true, 
                message: `✅ Product "${productTitle}" deleted successfully!`, 
                severity: "success" 
            });
            
            setDeleteDialog({ open: false, product: null });
            await loadProducts(); // Reload products after delete
        } catch (error) {
            console.error("Error deleting product:", error);
            setSnackbar({ 
                open: true, 
                message: `❌ Failed to delete product: ${error.message}`, 
                severity: "error" 
            });
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

    const handleSelectProduct = (product, checked) => {
        if (checked) {
            setSelectedProducts(prev => [...prev, product]);
        } else {
            setSelectedProducts(prev => prev.filter(p => p.productID !== product.productID));
        }
    };
    const handleSelectAll = (checked) => {
        if (checked) {
            setSelectedProducts([...filteredProducts]);
        } else {
            setSelectedProducts([]);
        }
    };

    const handleClearSelection = () => {
        setSelectedProducts([]);
    };

    const handleSelectByFilter = (filterType) => {
        let productsToSelect = [];
        switch (filterType) {
            case 'low-stock':
                productsToSelect = filteredProducts.filter(p => p.quantity > 0 && p.quantity <= 5);
                break;
            case 'out-of-stock':
                productsToSelect = filteredProducts.filter(p => p.quantity === 0);
                break;
            default:
                productsToSelect = [];
        }
        setSelectedProducts(productsToSelect);
    };

    const handleOpenBulkDeleteDialog = () => {
        setBulkDeleteDialog({ open: true, products: selectedProducts });
    };

    const handleBulkDelete = async () => {
        try {
            setIsDeleting(true);
            setDeleteProgress(0);
            
            const productIds = selectedProducts.map(p => p.productID);
            const totalProducts = productIds.length;
            
            // Simulate progress for better UX
            for (let i = 0; i <= totalProducts; i++) {
                setDeleteProgress((i / totalProducts) * 100);
                await new Promise(resolve => setTimeout(resolve, 100)); // Small delay for visual feedback
            }
            
            await productService.deleteProducts(productIds);
            
            setSnackbar({ 
                open: true, 
                message: `✅ Successfully deleted ${totalProducts} products!`, 
                severity: "success" 
            });
            
            setSelectedProducts([]);
            setBulkDeleteDialog({ open: false, products: [] });
            await loadProducts();
        } catch (error) {
            setSnackbar({ 
                open: true, 
                message: `❌ Failed to delete products: ${error.message}`, 
                severity: "error" 
            });
        } finally {
            setIsDeleting(false);
            setDeleteProgress(0);
        }
    };

    return (
        <Container maxWidth="xl" sx={{ py: 4 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 4 }}>
                <Typography variant="h4" component="h1" fontWeight="bold">
                    Product Management
                </Typography>
                <Box sx={{ display: "flex", gap: 2 }}>
                    <Button variant="contained" startIcon={<Add />} onClick={handleAdd}>
                        Add Product
                    </Button>
                </Box>
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
                                    <TableCell padding="checkbox">
                                        <Checkbox
                                            checked={selectedProducts.length === filteredProducts.length && filteredProducts.length > 0}
                                            indeterminate={selectedProducts.length > 0 && selectedProducts.length < filteredProducts.length}
                                            onChange={(e) => handleSelectAll(e.target.checked)}
                                        />
                                    </TableCell>
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
                                {filteredProducts.map((product) => {
                                    const isSelected = selectedProducts.some(p => p.productID === product.productID);
                                    return (
                                        <TableRow 
                                            key={product.productID} 
                                            hover 
                                            selected={isSelected}
                                            sx={{ 
                                                backgroundColor: isSelected ? 'rgba(25, 118, 210, 0.08)' : 'inherit',
                                                '&:hover': {
                                                    backgroundColor: isSelected ? 'rgba(25, 118, 210, 0.12)' : 'rgba(0, 0, 0, 0.04)'
                                                }
                                            }}
                                        >
                                            <TableCell padding="checkbox">
                                                <Checkbox
                                                    checked={isSelected}
                                                    onChange={(e) => handleSelectProduct(product, e.target.checked)}
                                                />
                                            </TableCell>
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
                                    );
                                })}
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

            {/* Batch Operations Panel */}
            <Slide direction="up" in={selectedProducts.length > 0} mountOnEnter unmountOnExit>
                <Paper
                    elevation={8}
                    sx={{
                        position: 'fixed',
                        bottom: 0,
                        left: 0,
                        right: 0,
                        zIndex: 1300,
                        borderRadius: '16px 16px 0 0',
                        backgroundColor: 'background.paper',
                        borderTop: '1px solid',
                        borderColor: 'divider',
                    }}
                >
                    <Box sx={{ p: 3 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                <CheckCircle color="primary" />
                                <Typography variant="h6" fontWeight="bold">
                                    {selectedProducts.length} Product{selectedProducts.length > 1 ? 's' : ''} Selected
                                </Typography>
                            </Box>
                            <IconButton onClick={handleClearSelection} size="small">
                                <Clear />
                            </IconButton>
                        </Box>
                        
                        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
                            <Button
                                variant="outlined"
                                startIcon={<SelectAll />}
                                onClick={() => handleSelectAll(true)}
                                size="small"
                                disabled={selectedProducts.length === filteredProducts.length}
                            >
                                Select All ({filteredProducts.length})
                            </Button>
                            
                            <Button
                                variant="outlined"
                                startIcon={<ClearAll />}
                                onClick={handleClearSelection}
                                size="small"
                            >
                                Clear Selection
                            </Button>
                            
                            <Divider orientation="vertical" flexItem />
                            
                            <Button
                                variant="outlined"
                                onClick={() => handleSelectByFilter('low-stock')}
                                size="small"
                                color="warning"
                            >
                                Select Low Stock
                            </Button>
                            
                            <Button
                                variant="outlined"
                                onClick={() => handleSelectByFilter('out-of-stock')}
                                size="small"
                                color="error"
                            >
                                Select Out of Stock
                            </Button>
                            
                            <Box sx={{ flexGrow: 1 }} />
                            
                            <Button
                                variant="contained"
                                color="error"
                                startIcon={<Delete />}
                                onClick={handleOpenBulkDeleteDialog}
                                disabled={selectedProducts.length === 0 || selectedProducts.length > 10}
                                size="large"
                            >
                                {selectedProducts.length > 10 
                                    ? `Cannot Delete (Max 10)` 
                                    : `Delete Selected (${selectedProducts.length})`
                                }
                            </Button>
                        </Box>
                        
                        {selectedProducts.length > 10 && (
                            <Alert severity="warning" sx={{ mt: 2 }}>
                                You can only delete up to 10 products at once. Please reduce your selection.
                            </Alert>
                        )}
                    </Box>
                </Paper>
            </Slide>

            {/* Bulk Delete Confirmation Dialog */}
            <Dialog 
                open={bulkDeleteDialog.open} 
                onClose={() => setBulkDeleteDialog({ open: false, products: [] })}
                maxWidth="md"
                fullWidth
            >
                <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Warning color="error" />
                    Confirm Bulk Delete
                </DialogTitle>
                <DialogContent>
                    <Typography variant="body1" sx={{ mb: 2 }}>
                        Are you sure you want to delete the following {bulkDeleteDialog.products.length} product{bulkDeleteDialog.products.length > 1 ? 's' : ''}? 
                        This action cannot be undone.
                    </Typography>
                    
                    {isDeleting && (
                        <Box sx={{ mb: 2 }}>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                Deleting products... {Math.round(deleteProgress)}%
                            </Typography>
                            <LinearProgress variant="determinate" value={deleteProgress} />
                        </Box>
                    )}
                    
                    <Paper variant="outlined" sx={{ maxHeight: 300, overflow: 'auto' }}>
                        <List dense>
                            {bulkDeleteDialog.products.map((product, index) => (
                                <ListItem key={product.productID} divider={index < bulkDeleteDialog.products.length - 1}>
                                    <ListItemAvatar>
                                        <Avatar
                                            src={product.imageURL}
                                            alt={product.title}
                                            variant="rounded"
                                            sx={{ width: 40, height: 40 }}
                                        />
                                    </ListItemAvatar>
                                    <ListItemText
                                        primary={product.title}
                                        secondary={`ID: ${product.productID} | ${product.category?.toUpperCase()} | Stock: ${product.quantity}`}
                                    />
                                    <Chip
                                        label={getStockStatus(product.quantity).label}
                                        color={getStockStatus(product.quantity).color}
                                        size="small"
                                    />
                                </ListItem>
                            ))}
                        </List>
                    </Paper>
                </DialogContent>
                <DialogActions>
                    <Button 
                        onClick={() => setBulkDeleteDialog({ open: false, products: [] })}
                        disabled={isDeleting}
                    >
                        Cancel
                    </Button>
                    <Button 
                        onClick={handleBulkDelete} 
                        color="error" 
                        variant="contained"
                        disabled={isDeleting}
                        startIcon={isDeleting ? <CircularProgress size={20} /> : <Delete />}
                    >
                        {isDeleting ? 'Deleting...' : 'Delete All'}
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

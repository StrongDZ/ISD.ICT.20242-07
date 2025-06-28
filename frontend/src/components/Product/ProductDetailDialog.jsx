import React from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Grid,
    Typography,
    Box,
    Chip,
    Divider,
    Card,
    CardContent,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableRow,
    Paper,
} from "@mui/material";
import { Close, Edit, Inventory, TrendingUp, TrendingDown, Warning } from "@mui/icons-material";
import { getCategoryColor } from "../../utils/getCategoryColor";

const ProductDetailDialog = ({ open, onClose, product, onEdit }) => {
    if (!product) return null;

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const formatDate = (date) => {
        if (!date) return "N/A";
        return new Date(date).toLocaleDateString("vi-VN");
    };


    const getStockStatus = (quantity) => {
        if (quantity === 0) {
            return { color: "error", label: "Out of Stock", icon: <Warning /> };
        } else if (quantity <= 5) {
            return { color: "warning", label: "Low Stock", icon: <TrendingDown /> };
        } else if (quantity <= 10) {
            return { color: "info", label: "Medium Stock", icon: <TrendingUp /> };
        }
        return { color: "success", label: "In Stock", icon: <Inventory /> };
    };

    const stockStatus = getStockStatus(product.quantity);

    const renderCategorySpecificInfo = () => {
        switch (product.category) {
            case "book":
                return (
                    <Card sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom color="primary">
                                Book Information
                            </Typography>
                            <TableContainer>
                                <Table size="small">
                                    <TableBody>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Author(s)</strong>
                                            </TableCell>
                                            <TableCell>{product.authors || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Publisher</strong>
                                            </TableCell>
                                            <TableCell>{product.publisher || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Pages</strong>
                                            </TableCell>
                                            <TableCell>{product.numberOfPages || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Language</strong>
                                            </TableCell>
                                            <TableCell>{product.language || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Genre</strong>
                                            </TableCell>
                                            <TableCell>{product.genre || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Cover Type</strong>
                                            </TableCell>
                                            <TableCell>{product.coverType || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>ISBN</strong>
                                            </TableCell>
                                            <TableCell>{product.isbn || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Publication Date</strong>
                                            </TableCell>
                                            <TableCell>{formatDate(product.pubDate)}</TableCell>
                                        </TableRow>
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </CardContent>
                    </Card>
                );

            case "cd":
                return (
                    <Card sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom color="warning.main">
                                Album Information
                            </Typography>
                            <TableContainer>
                                <Table size="small">
                                    <TableBody>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Artist</strong>
                                            </TableCell>
                                            <TableCell>{product.artist || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Record Label</strong>
                                            </TableCell>
                                            <TableCell>{product.recordLabel || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Music Type</strong>
                                            </TableCell>
                                            <TableCell>{product.musicType || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Release Date</strong>
                                            </TableCell>
                                            <TableCell>{formatDate(product.releaseDate)}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Tracklist</strong>
                                            </TableCell>
                                            <TableCell>
                                                <Typography variant="body2" style={{ whiteSpace: "pre-line" }}>
                                                    {product.tracklist || "N/A"}
                                                </Typography>
                                            </TableCell>
                                        </TableRow>
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </CardContent>
                    </Card>
                );

            case "dvd":
                return (
                    <Card sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom color="secondary.main">
                                Movie Information
                            </Typography>
                            <TableContainer>
                                <Table size="small">
                                    <TableBody>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Director</strong>
                                            </TableCell>
                                            <TableCell>{product.director || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Studio</strong>
                                            </TableCell>
                                            <TableCell>{product.studio || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Runtime</strong>
                                            </TableCell>
                                            <TableCell>{product.runtime || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Disc Type</strong>
                                            </TableCell>
                                            <TableCell>{product.discType || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Language</strong>
                                            </TableCell>
                                            <TableCell>{product.language || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Genre</strong>
                                            </TableCell>
                                            <TableCell>{product.genre || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Subtitles</strong>
                                            </TableCell>
                                            <TableCell>{product.subtitle || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Release Date</strong>
                                            </TableCell>
                                            <TableCell>{formatDate(product.releaseDate)}</TableCell>
                                        </TableRow>
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </CardContent>
                    </Card>
                );

            case "lp":
                return (
                    <Card sx={{ mt: 2 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom color="success.main">
                                Vinyl Information
                            </Typography>
                            <TableContainer>
                                <Table size="small">
                                    <TableBody>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Artist</strong>
                                            </TableCell>
                                            <TableCell>{product.artist || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Record Label</strong>
                                            </TableCell>
                                            <TableCell>{product.recordLabel || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Vinyl Size</strong>
                                            </TableCell>
                                            <TableCell>{product.vinylSize || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>RPM</strong>
                                            </TableCell>
                                            <TableCell>{product.rpm || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Music Type</strong>
                                            </TableCell>
                                            <TableCell>{product.musicType || "N/A"}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Release Date</strong>
                                            </TableCell>
                                            <TableCell>{formatDate(product.releaseDate)}</TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell>
                                                <strong>Tracklist</strong>
                                            </TableCell>
                                            <TableCell>
                                                <Typography variant="body2" style={{ whiteSpace: "pre-line" }}>
                                                    {product.tracklist || "N/A"}
                                                </Typography>
                                            </TableCell>
                                        </TableRow>
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </CardContent>
                    </Card>
                );

            default:
                return null;
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth PaperProps={{ sx: { minHeight: "70vh" } }}>
            <DialogTitle>
                <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <Typography variant="h6">Product Details</Typography>
                    <Chip label={product.category?.toUpperCase()} color={getCategoryColor(product.category)} size="small" />
                </Box>
            </DialogTitle>

            <DialogContent>
                <Grid container spacing={3}>
                    {/* Product Image and Basic Info */}
                    <Grid item xs={12} md={4}>
                        <Box sx={{ textAlign: "center" }}>
                            <img
                                src={product.imageURL}
                                alt={product.title}
                                style={{
                                    width: "100%",
                                    maxWidth: "250px",
                                    height: "300px",
                                    objectFit: "cover",
                                    borderRadius: "8px",
                                    border: "1px solid #e0e0e0",
                                }}
                            />
                            <Box sx={{ mt: 2 }}>
                                <Chip icon={stockStatus.icon} label={stockStatus.label} color={stockStatus.color} sx={{ mb: 1 }} />
                                <Typography variant="h6" color={stockStatus.color + ".main"}>
                                    {product.quantity} units available
                                </Typography>
                            </Box>
                        </Box>
                    </Grid>

                    {/* Product Details */}
                    <Grid item xs={12} md={8}>
                        <Typography variant="h5" gutterBottom sx={{ fontWeight: "bold" }}>
                            {product.title}
                        </Typography>

                        <Typography variant="h6" color="primary" sx={{ mb: 2 }}>
                            {formatPrice(product.price)}
                        </Typography>

                        {product.description && (
                            <Box sx={{ mb: 2 }}>
                                <Typography variant="body1" paragraph>
                                    {product.description}
                                </Typography>
                            </Box>
                        )}

                        <Divider sx={{ my: 2 }} />

                        {/* Product Specifications */}
                        <Card variant="outlined">
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Product Specifications
                                </Typography>
                                <TableContainer>
                                    <Table size="small">
                                        <TableBody>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Product ID</strong>
                                                </TableCell>
                                                <TableCell>{product.productID}</TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Category</strong>
                                                </TableCell>
                                                <TableCell>
                                                    <Chip
                                                        label={product.category?.toUpperCase()}
                                                        color={getCategoryColor(product.category)}
                                                        size="small"
                                                    />
                                                </TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Price</strong>
                                                </TableCell>
                                                <TableCell>{formatPrice(product.price)}</TableCell>
                                            </TableRow>
                                            {product.value && product.value !== product.price && (
                                                <TableRow>
                                                    <TableCell>
                                                        <strong>Value</strong>
                                                    </TableCell>
                                                    <TableCell>{formatPrice(product.value)}</TableCell>
                                                </TableRow>
                                            )}
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Stock Quantity</strong>
                                                </TableCell>
                                                <TableCell>
                                                    <Typography color={stockStatus.color + ".main"} sx={{ fontWeight: "bold" }}>
                                                        {product.quantity}
                                                    </Typography>
                                                </TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Weight</strong>
                                                </TableCell>
                                                <TableCell>{product.weight ? `${product.weight} kg` : "N/A"}</TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Dimensions</strong>
                                                </TableCell>
                                                <TableCell>{product.dimensions || "N/A"}</TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Barcode</strong>
                                                </TableCell>
                                                <TableCell>{product.barcode || "N/A"}</TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell>
                                                    <strong>Rush Eligible</strong>
                                                </TableCell>
                                                <TableCell>
                                                    <Chip
                                                        label={product.isRushEligible || product.eligible ? "Yes" : "No"}
                                                        color={product.isRushEligible || product.eligible ? "success" : "default"}
                                                        size="small"
                                                    />
                                                </TableCell>
                                            </TableRow>
                                            {product.warehouseEntryDate && (
                                                <TableRow>
                                                    <TableCell>
                                                        <strong>Entry Date</strong>
                                                    </TableCell>
                                                    <TableCell>{formatDate(product.warehouseEntryDate)}</TableCell>
                                                </TableRow>
                                            )}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>

                {/* Category Specific Information */}
                {renderCategorySpecificInfo()}
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} startIcon={<Close />}>
                    Close
                </Button>
                {onEdit && (
                    <Button onClick={() => onEdit(product)} variant="contained" startIcon={<Edit />}>
                        Edit Product
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default ProductDetailDialog;

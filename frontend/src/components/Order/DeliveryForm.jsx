import React, { useState } from "react";
import {
    Card,
    CardContent,
    Typography,
    Grid,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    FormControlLabel,
    Checkbox,
    Alert,
    Box,
} from "@mui/material";
import { LocalShipping, Speed } from "@mui/icons-material";

const DeliveryForm = ({ deliveryInfo, onDeliveryInfoChange, errors = {} }) => {
    const vietnamCities = [
        "Hà Nội",
        "Hồ Chí Minh",
        "Đà Nẵng",
        "Hải Phòng",
        "Cần Thơ",
        "An Giang",
        "Bà Rịa - Vũng Tàu",
        "Bắc Giang",
        "Bắc Kạn",
        "Bạc Liêu",
        "Bắc Ninh",
        "Bến Tre",
        "Bình Định",
        "Bình Dương",
        "Bình Phước",
        "Bình Thuận",
        "Cà Mau",
        "Cao Bằng",
        "Đắk Lắk",
        "Đắk Nông",
        "Điện Biên",
        "Đồng Nai",
        "Đồng Tháp",
        "Gia Lai",
        "Hà Giang",
        "Hà Nam",
        "Hà Tĩnh",
        "Hải Dương",
        "Hậu Giang",
        "Hòa Bình",
        "Hưng Yên",
        "Khánh Hòa",
        "Kiên Giang",
        "Kon Tum",
        "Lai Châu",
        "Lâm Đồng",
        "Lạng Sơn",
        "Lào Cai",
        "Long An",
        "Nam Định",
        "Nghệ An",
        "Ninh Bình",
        "Ninh Thuận",
        "Phú Thọ",
        "Phú Yên",
        "Quảng Bình",
        "Quảng Nam",
        "Quảng Ngãi",
        "Quảng Ninh",
        "Quảng Trị",
        "Sóc Trăng",
        "Sơn La",
        "Tây Ninh",
        "Thái Bình",
        "Thái Nguyên",
        "Thanh Hóa",
        "Thừa Thiên Huế",
        "Tiền Giang",
        "Trà Vinh",
        "Tuyên Quang",
        "Vĩnh Long",
        "Vĩnh Phúc",
        "Yên Bái",
    ];

    const hanoiDistricts = [
        "Ba Đình",
        "Hoàn Kiếm",
        "Tây Hồ",
        "Long Biên",
        "Cầu Giấy",
        "Đống Đa",
        "Hai Bà Trưng",
        "Hoàng Mai",
        "Thanh Xuân",
        "Sóc Sơn",
        "Đông Anh",
        "Gia Lâm",
        "Nam Từ Liêm",
        "Bắc Từ Liêm",
        "Mê Linh",
        "Hà Đông",
        "Sơn Tây",
        "Ba Vì",
        "Phúc Thọ",
        "Đan Phượng",
        "Hoài Đức",
        "Quốc Oai",
        "Thạch Thất",
        "Chương Mỹ",
        "Thanh Oai",
        "Thường Tín",
        "Phú Xuyên",
        "Ứng Hòa",
        "Mỹ Đức",
    ];

    const handleFieldChange = (field, value) => {
        onDeliveryInfoChange({
            ...deliveryInfo,
            [field]: value,
        });
    };

    const isRushEligible = deliveryInfo.city === "Hà Nội" && hanoiDistricts.includes(deliveryInfo.district);

    return (
        <Card>
            <CardContent>
                <Typography variant="h6" gutterBottom>
                    Delivery Information
                </Typography>

                <Grid container spacing={3}>
                    {/* Recipient Information */}
                    <Grid item xs={12}>
                        <Typography variant="subtitle1" gutterBottom sx={{ mt: 2, fontWeight: "medium" }}>
                            Recipient Details
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Full Name *"
                            value={deliveryInfo.recipientName || ""}
                            onChange={(e) => handleFieldChange("recipientName", e.target.value)}
                            error={!!errors.recipientName}
                            helperText={errors.recipientName}
                            required
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Phone Number *"
                            value={deliveryInfo.phoneNumber || ""}
                            onChange={(e) => handleFieldChange("phoneNumber", e.target.value)}
                            error={!!errors.phoneNumber}
                            helperText={errors.phoneNumber}
                            required
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Email"
                            type="email"
                            value={deliveryInfo.mail || ""}
                            onChange={(e) => handleFieldChange("mail", e.target.value)}
                            error={!!errors.mail}
                            helperText={errors.mail}
                        />
                    </Grid>

                    {/* Address Information */}
                    <Grid item xs={12}>
                        <Typography variant="subtitle1" gutterBottom sx={{ mt: 2, fontWeight: "medium" }}>
                            Delivery Address
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth error={!!errors.city}>
                            <InputLabel>City/Province *</InputLabel>
                            <Select
                                value={deliveryInfo.city || ""}
                                onChange={(e) => {
                                    handleFieldChange("city", e.target.value);
                                    // Reset district when city changes
                                    if (e.target.value !== "Hà Nội") {
                                        handleFieldChange("district", "");
                                    }
                                }}
                                label="City/Province *"
                                required
                            >
                                {vietnamCities.map((city) => (
                                    <MenuItem key={city} value={city}>
                                        {city}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth error={!!errors.district}>
                            <InputLabel>District *</InputLabel>
                            <Select
                                value={deliveryInfo.district || ""}
                                onChange={(e) => handleFieldChange("district", e.target.value)}
                                label="District *"
                                disabled={!deliveryInfo.city}
                                required
                            >
                                {deliveryInfo.city === "Hà Nội" ? (
                                    hanoiDistricts.map((district) => (
                                        <MenuItem key={district} value={district}>
                                            {district}
                                        </MenuItem>
                                    ))
                                ) : (
                                    <MenuItem value="Other">Other District</MenuItem>
                                )}
                            </Select>
                        </FormControl>
                    </Grid>

                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Detailed Address *"
                            multiline
                            rows={2}
                            value={deliveryInfo.addressDetail || ""}
                            onChange={(e) => handleFieldChange("addressDetail", e.target.value)}
                            error={!!errors.addressDetail}
                            helperText={errors.addressDetail || "Street name, house number, etc."}
                            required
                        />
                    </Grid>

                    {/* Delivery Options */}
                    <Grid item xs={12}>
                        <Typography variant="subtitle1" gutterBottom sx={{ mt: 2, fontWeight: "medium" }}>
                            Delivery Options
                        </Typography>
                    </Grid>

                    <Grid item xs={12}>
                        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={!deliveryInfo.isRushOrder}
                                        onChange={(e) => handleFieldChange("isRushOrder", !e.target.checked)}
                                        icon={<LocalShipping />}
                                        checkedIcon={<LocalShipping color="primary" />}
                                    />
                                }
                                label={
                                    <Box>
                                        <Typography variant="body1">Standard Delivery (3-5 days)</Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Free shipping - Delivered within 3-5 business days
                                        </Typography>
                                    </Box>
                                }
                            />

                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={deliveryInfo.isRushOrder}
                                        onChange={(e) => handleFieldChange("isRushOrder", e.target.checked)}
                                        disabled={!isRushEligible}
                                        icon={<Speed />}
                                        checkedIcon={<Speed color="success" />}
                                    />
                                }
                                label={
                                    <Box>
                                        <Typography variant="body1">
                                            Rush Delivery (Same day)
                                            {!isRushEligible && (
                                                <Typography component="span" color="text.secondary">
                                                    {" "}
                                                    - Not available
                                                </Typography>
                                            )}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {isRushEligible
                                                ? "Additional 50,000 VND - Delivered within 24 hours"
                                                : "Only available in Hanoi city center"}
                                        </Typography>
                                    </Box>
                                }
                            />
                        </Box>

                        {!isRushEligible && deliveryInfo.city && (
                            <Alert severity="info" sx={{ mt: 2 }}>
                                <Typography variant="body2">
                                    Rush delivery is currently only available in Hanoi city center districts.
                                    {deliveryInfo.city !== "Hà Nội" && " Please select Hanoi as your city to enable rush delivery."}
                                </Typography>
                            </Alert>
                        )}
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};

export default DeliveryForm;

import React from "react";
import { Container, Typography, Button, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

const PaymentDecline = () => {
    const navigate = useNavigate();

    const handleReturnHome = () => {
        navigate("/");
    };

    return (
        <Container maxWidth="sm" sx={{ mt: 10, textAlign: "center" }}>
            <Typography variant="h4" color="error" gutterBottom>
                ⚠️ Giao dịch bị từ chối!
            </Typography>
            <Typography variant="body1" sx={{ mt: 2 }}>
                Giao dịch không thành công do: Khách hàng hủy giao dịch. Vui lòng thử lại hoặc sử dụng phương thức thanh toán khác.
            </Typography>

            <Box sx={{ mt: 4 }}>
                <Button variant="contained" color="primary" onClick={handleReturnHome}>
                    Quay lại trang chủ
                </Button>
            </Box>
        </Container>
    );
};

export default PaymentDecline;

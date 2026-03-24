import './style.css';
import {useState} from "react";
export function App() {
    const [paymentId, setPaymentId] = useState('');
    const onClick = ()=> {
        console.log("хехе,бой")
        fetch('https://videos.cloudpub.ru/api/payment/buy', {
            method:'POST',
            headers: {
                'Content-Type':'application/json'
            },
            body: JSON.stringify({
                orderId:10,
                userId:'userId'
            }),
        }).then(res => res.json())
            .then(data => {
                console.log(data)
                window.open(data?.confirmation?.confirmation_url, '_blank')

            })
    }

    const onGetPayment = () => {
        fetch('https://videos.cloudpub.ru/api/payment/get', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                paymentid: paymentId  // поле должно называться paymentid (как в record)
            })
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                console.log('Payment info:', data);
                alert(`Статус: ${data.status}\nОплачен: ${data.paid ? 'Да' : 'Нет'}`);
            })
            .catch(error => {
                console.error('Error getting payment:', error);
                alert(`Ошибка: ${error.message}`);
            });
    };

    return (
        <div>
            <button onClick={onClick}>Купи, сука</button>

            <div >
                <label>ID платежа (UUID):</label>
                <input
                    type="text"
                    value={paymentId}
                    onChange={(e) => setPaymentId(e.target.value)}
                />
            </div>

            <button onClick={onGetPayment}> Проверить платеж </button>
        </div>
    );
}

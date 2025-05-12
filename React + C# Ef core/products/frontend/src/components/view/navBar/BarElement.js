function BarElement({title,isActive, onClick }) {
    return(<div className="button-borders">
            <button  className={`primary-button ${isActive ? 'active' : ''}`} onClick={onClick}>
                {title}
            </button>
    </div>

        )
}
export {BarElement}
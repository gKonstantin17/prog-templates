using File_server.Entity;
using File_server.Repository;

namespace File_server.Service
{
    public class UserService
    {
        private UserRepository repository;
        public UserService(UserRepository repository)
        {
            this.repository = repository;
        }
        // регистрация
        public async Task<User> register(UserDto dto) => await repository.create(dto);

        // найти по логину
        public async Task<User> findByLogin(UserDto dto) => await repository.findByLogin(dto);
        public async Task<User> findById(long id) => await repository.findById(id);

        // аутентификация
        public async Task<User> login(UserDto dto)
        {
            User user = await repository.findByLogin(dto);
            if (user == null || user.Password != dto.Password)
                return null;
            return user;
        }

        // изменить фото (в бд)
        public async Task<User> changePhoto(User user, FileData photo) => await repository.changePhoto(user, photo);

    }
}

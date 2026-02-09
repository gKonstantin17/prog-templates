using File_server.Entity;
using Microsoft.EntityFrameworkCore;

namespace File_server.Repository
{
    public class UserRepository
    {
        private readonly ApplicationContext _db;
        public UserRepository(ApplicationContext db)
        {
            _db = db;
        }
        // создать
        public async Task<User> create(UserDto dto)
        {
            User user = new User
            {
                Name = dto.Name,
                Login = dto.Login,
                Password = dto.Password
            };
            var result = await _db.User.AddAsync(user);
            await _db.SaveChangesAsync();
            return result.Entity;
        }

        // найти
        public async Task<User> findByLogin(UserDto dto) => await _db.User.FirstAsync(u => u.Login == dto.Login);
        public async Task<User> findById(long id) => await _db.User.FindAsync(id);

        // изменить фото
        public async Task<User> changePhoto(User user, FileData photo)
        {
            user.Photo = photo?.Path;
            await _db.SaveChangesAsync();
            return user;
        }

    }
}
